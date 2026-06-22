import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { Line } from '@react-three/drei';
import * as THREE from 'three';

const PRIMARY = '#f59e0b';
const PRIMARY_LIGHT = '#fbbf24';
const GLOW = '#fcd34d';
const R = 1;

const GEO_URL = 'https://cdn.jsdelivr.net/gh/nvkelso/natural-earth-vector@master/geojson/ne_110m_land.geojson';

interface City {
  name: string;
  lat: number;
  lng: number;
}

const cities: City[] = [
  { name: 'Dubai', lat: 25.2, lng: 55.3 },
  { name: 'London', lat: 51.5, lng: -0.1 },
  { name: 'New York', lat: 40.7, lng: -74.0 },
  { name: 'Singapore', lat: 1.3, lng: 103.8 },
  { name: 'Tokyo', lat: 35.7, lng: 139.7 },
  { name: 'Mumbai', lat: 19.1, lng: 72.9 },
  { name: 'Sydney', lat: -33.9, lng: 151.2 },
];

const hubConnections: [number, number][] = [
  [5, 0], [5, 1], [5, 2], [5, 3], [5, 4], [5, 6],
];

const secondaryArcs: [number, number][] = [
  [1, 2], [2, 3], [3, 4], [2, 6],
];

function latLngToPos(lat: number, lng: number, radius: number): THREE.Vector3 {
  const phi = (90 - lat) * (Math.PI / 180);
  const theta = lng * (Math.PI / 180);
  return new THREE.Vector3(
    radius * Math.sin(phi) * Math.cos(theta),
    radius * Math.cos(phi),
    radius * Math.sin(phi) * Math.sin(theta),
  );
}

function useContinentsTexture() {
  const [texture, setTexture] = useState<THREE.CanvasTexture | null>(null);

  useEffect(() => {
    let cancelled = false;
    fetch(GEO_URL)
      .then(r => r.json())
      .then((geoJson: any) => {
        if (cancelled) return;
        const w = 1024, h = 512;
        const canvas = document.createElement('canvas');
        canvas.width = w;
        canvas.height = h;
        const ctx = canvas.getContext('2d')!;
        ctx.clearRect(0, 0, w, h);
        ctx.fillStyle = PRIMARY;

        function drawRing(ring: number[][]) {
          ctx.beginPath();
          for (let i = 0; i < ring.length; i++) {
            const [lng, lat] = ring[i];
            const x = ((lng + 180) / 360) * w;
            const y = ((90 - lat) / 180) * h;
            i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
          }
          ctx.closePath();
          ctx.fill();
        }

        for (const feature of geoJson.features) {
          const geom = feature.geometry;
          if (geom.type === 'Polygon') {
            geom.coordinates.forEach(drawRing);
          } else if (geom.type === 'MultiPolygon') {
            geom.coordinates.forEach((poly: number[][][]) => poly.forEach(drawRing));
          }
        }

        const tex = new THREE.CanvasTexture(canvas);
        tex.needsUpdate = true;
        setTexture(tex);
      })
      .catch(() => {});

    return () => { cancelled = true; };
  }, []);

  return texture;
}

function Continents() {
  const tex = useContinentsTexture();
  if (!tex) return null;
  return (
    <mesh>
      <sphereGeometry args={[R - 0.002, 48, 48]} />
      <meshBasicMaterial map={tex} transparent opacity={0.35} />
    </mesh>
  );
}

function CityDot({ lat, lng }: { lat: number; lng: number }) {
  const pos = useMemo(() => latLngToPos(lat, lng, R * 1.005), [lat, lng]);

  return (
    <group position={pos}>
      <mesh>
        <sphereGeometry args={[0.045, 12, 12]} />
        <meshBasicMaterial color={GLOW} transparent opacity={0.35} />
      </mesh>
      <mesh>
        <sphereGeometry args={[0.025, 12, 12]} />
        <meshBasicMaterial color={PRIMARY_LIGHT} />
      </mesh>
    </group>
  );
}

function ArcLine({ from, to }: { from: City; to: City }) {
  const points = useMemo(() => {
    const start = latLngToPos(from.lat, from.lng, R);
    const end = latLngToPos(to.lat, to.lng, R);
    const mid = new THREE.Vector3().addVectors(start, end).multiplyScalar(0.5);
    mid.normalize().multiplyScalar(R * 1.5);
    return new THREE.QuadraticBezierCurve3(start, mid, end).getPoints(32);
  }, [from, to]);

  return (
    <Line points={points} color={PRIMARY} transparent opacity={0.3} lineWidth={0.5} />
  );
}

function AnimatedDot({ from, to, speed }: { from: City; to: City; speed: number }) {
  const dotRef = useRef<THREE.Mesh>(null);
  const curveRef = useRef<THREE.QuadraticBezierCurve3 | null>(null);
  const tRef = useRef(Math.random());

  useMemo(() => {
    const start = latLngToPos(from.lat, from.lng, R);
    const end = latLngToPos(to.lat, to.lng, R);
    const mid = new THREE.Vector3().addVectors(start, end).multiplyScalar(0.5);
    mid.normalize().multiplyScalar(R * 1.5);
    curveRef.current = new THREE.QuadraticBezierCurve3(start, mid, end);
  }, [from, to]);

  useFrame((_, delta) => {
    if (!curveRef.current || !dotRef.current) return;
    tRef.current += delta * speed;
    if (tRef.current > 1) tRef.current = 0;
    dotRef.current.position.copy(curveRef.current.getPoint(tRef.current));
  });

  return (
    <mesh ref={dotRef}>
      <sphereGeometry args={[0.02, 8, 8]} />
      <meshBasicMaterial color={GLOW} />
    </mesh>
  );
}

function GlobeContent({ positionX = 0 }: { positionX?: number }) {
  const groupRef = useRef<THREE.Group>(null);

  useFrame(() => {
    if (groupRef.current) {
      groupRef.current.rotation.y += 0.0015;
      groupRef.current.rotation.x = 0.15;
    }
  });

  return (
    <group ref={groupRef} position={[positionX, 0, 0]}>
      <Continents />

      <mesh>
        <sphereGeometry args={[R, 24, 16]} />
        <meshBasicMaterial color={PRIMARY} wireframe transparent opacity={0.15} />
      </mesh>

      {hubConnections.map(([i, j], idx) => (
        <ArcLine key={`hub-arc-${idx}`} from={cities[i]} to={cities[j]} />
      ))}
      {secondaryArcs.map(([i, j], idx) => (
        <ArcLine key={`sec-arc-${idx}`} from={cities[i]} to={cities[j]} />
      ))}

      {cities.map((city, idx) => (
        <CityDot key={`city-${idx}`} lat={city.lat} lng={city.lng} />
      ))}

      {hubConnections.map(([i, j], idx) => (
        <AnimatedDot key={`anim-${idx}`} from={cities[i]} to={cities[j]} speed={0.12 + Math.random() * 0.08} />
      ))}
    </group>
  );
}

interface RotatingEarthProps {
  positionX?: number;
}

export const RotatingEarth: React.FC<RotatingEarthProps> = ({ positionX = 0 }) => (
  <div className="absolute inset-0 z-0 pointer-events-none overflow-hidden">
    <Canvas
      camera={{ position: [0, 0, 3.5], fov: 40 }}
      dpr={[1, 1.5]}
      gl={{ alpha: true }}
      className="opacity-80"
    >
      <GlobeContent positionX={positionX} />
    </Canvas>
  </div>
);
