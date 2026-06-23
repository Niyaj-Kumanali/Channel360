export interface Region {
  id: number;
  name: string;
  parentId: number | null;
  level: string;
  treeType: string;
  path: string;
  createdBy: string;
  updatedBy: string;
}

export interface RegionRequest {
  name: string;
  parentId: number | null;
  level: string;
  treeType: string;
}
