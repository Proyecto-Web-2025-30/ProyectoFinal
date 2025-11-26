import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  let authReq = req;

  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si no es 401, o es un endpoint de login/refresh, no intentamos refrescar
      if (
        error.status !== 401 ||
        req.url.includes('/auth/login') ||
        req.url.includes('/auth/refresh')
      ) {
        return throwError(() => error);
      }

      // Intentamos refrescar el token y reintentar la petición original una vez
      return authService.refreshToken().pipe(
        switchMap((newToken: string) => {
          const retryReq = req.clone({
            setHeaders: {
              Authorization: `Bearer ${newToken}`
            }
          });
          return next(retryReq);
        }),
        catchError(refreshError => {
          // Si el refresh falla, cerramos sesión y propagamos el error
          authService.logout();
          return throwError(() => refreshError);
        })
      );
    })
  );
};
