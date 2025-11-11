import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptors, HttpHandlerFn, HttpRequest } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';

// Interceptor funcional para adjuntar el Bearer
export function authInterceptorFn(req: HttpRequest<any>, next: HttpHandlerFn) {
  const token = localStorage.getItem('access_token');
  if (token) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
}

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([authInterceptorFn])),
    provideRouter(routes)
  ]
});
