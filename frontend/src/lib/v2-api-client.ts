import { ApiError } from './api-error';
import { authStorage } from './storage';

const V2_API_BASE_URL = import.meta.env.VITE_API_URL_V2 || 'http://localhost:5000/api';

class V2ApiClient {
  private baseUrl: string;

  constructor(baseUrl: string = V2_API_BASE_URL) {
    this.baseUrl = baseUrl;
  }

  private getToken(): string | null {
    return authStorage.get('access_token');
  }

  private getRefreshToken(): string | null {
    return authStorage.get('refresh_token');
  }

  setTokens(accessToken: string, refreshToken: string): void {
    authStorage.set('access_token', accessToken);
    authStorage.set('refresh_token', refreshToken);
  }

  clearTokens(): void {
    authStorage.remove('access_token');
    authStorage.remove('refresh_token');
    authStorage.remove('user');
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const token = this.getToken();
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...((options.headers as Record<string, string>) || {}),
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      ...options,
      headers,
    });

    if (response.status === 401 && token) {
      const refreshed = await this.tryRefreshToken();
      if (refreshed) {
        const newToken = this.getToken();
        headers['Authorization'] = `Bearer ${newToken}`;
        const retryResponse = await fetch(`${this.baseUrl}${endpoint}`, {
          ...options,
          headers,
        });
        const data = await retryResponse.json();
        if (!retryResponse.ok) {
          throw new ApiError(data.message || `Request failed (${retryResponse.status})`, { errors: data.errors, statusCode: retryResponse.status });
        }
        return data as T;
      }
      this.clearTokens();
      window.location.href = '/login';
      throw new Error('Session expired');
    }

    const data = await response.json();

    if (!response.ok) {
      throw new ApiError(data.message || `Request failed (${response.status})`, { errors: data.errors, statusCode: response.status });
    }

    return data as T;
  }

  private async tryRefreshToken(): Promise<boolean> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) return false;

    try {
      const response = await fetch(`${this.baseUrl}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken }),
      });
      const data = await response.json();
      if (response.ok && data.success) {
        this.setTokens(data.data.accessToken, data.data.accessToken);
        return true;
      }
      return false;
    } catch {
      return false;
    }
  }

  async get<T>(endpoint: string, params?: Record<string, string>): Promise<T> {
    let url = endpoint;
    if (params) {
      const searchParams = new URLSearchParams(params);
      url += `?${searchParams.toString()}`;
    }
    return this.request<T>(url, { method: 'GET' });
  }

  async post<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async put<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async patch<T>(endpoint: string, data?: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PATCH',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  async delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, { method: 'DELETE' });
  }
}

export const v2ApiClient = new V2ApiClient();
