import { HttpClient } from '@angular/common/http';
import { computed, inject, Service, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../api/api.config';
import { AuthSession, LoginRequest, LoginResponse } from './auth.models';

const SESSION_STORAGE_KEY = 'acme.salary-management.session';

@Service()
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly session = signal<AuthSession | null>(this.readSession());

  readonly isAuthenticated = computed(() => {
    const currentSession = this.session();
    return currentSession !== null && currentSession.expiresAt > Date.now();
  });

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${API_BASE_URL}/api/auth/login`, credentials)
      .pipe(tap((response) => this.saveSession(response)));
  }

  getAccessToken(): string | null {
    const currentSession = this.session();
    if (!currentSession || currentSession.expiresAt <= Date.now()) {
      this.logout();
      return null;
    }
    return currentSession.accessToken;
  }

  getAuthorizationHeader(): string | null {
    const currentSession = this.session();
    const token = this.getAccessToken();
    return token && currentSession ? `${currentSession.tokenType} ${token}` : null;
  }

  logout(): void {
    this.session.set(null);
    sessionStorage.removeItem(SESSION_STORAGE_KEY);
  }

  private saveSession(response: LoginResponse): void {
    const newSession: AuthSession = {
      ...response,
      expiresAt: Date.now() + response.expiresInSeconds * 1000,
    };
    sessionStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(newSession));
    this.session.set(newSession);
  }

  private readSession(): AuthSession | null {
    const value = sessionStorage.getItem(SESSION_STORAGE_KEY);
    if (!value) {
      return null;
    }

    try {
      const parsed: unknown = JSON.parse(value);
      if (this.isAuthSession(parsed) && parsed.expiresAt > Date.now()) {
        return parsed;
      }
    } catch {
      // Ignore malformed or stale browser session data and require sign-in.
    }
    sessionStorage.removeItem(SESSION_STORAGE_KEY);
    return null;
  }

  private isAuthSession(value: unknown): value is AuthSession {
    if (typeof value !== 'object' || value === null) {
      return false;
    }
    const session = value as Partial<AuthSession>;
    return typeof session.accessToken === 'string'
      && typeof session.tokenType === 'string'
      && typeof session.expiresInSeconds === 'number'
      && typeof session.expiresAt === 'number';
  }
}
