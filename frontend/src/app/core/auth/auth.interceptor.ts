import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { API_BASE_URL } from '../api/api.config';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const authorization = authService.getAuthorizationHeader();
  const requestUrl = new URL(request.url, API_BASE_URL);
  const apiOrigin = new URL(API_BASE_URL).origin;
  const isLoginRequest = requestUrl.pathname.endsWith('/api/auth/login');

  if (!authorization || isLoginRequest || requestUrl.origin !== apiOrigin) {
    return next(request);
  }

  return next(request.clone({
    setHeaders: { Authorization: authorization },
  }));
};
