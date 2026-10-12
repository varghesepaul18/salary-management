import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login-page/login-page').then((module) => module.LoginPage),
  },
  {
    path: 'app',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/app-shell/app-shell').then((module) => module.AppShell),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'employees' },
      {
        path: 'employees',
        loadComponent: () => import('./shared/feature-placeholder/feature-placeholder').then((module) => module.FeaturePlaceholder),
        data: {
          title: 'Employee directory',
          description: 'Search and review employee details across the organization.',
        },
      },
      {
        path: 'analytics',
        loadComponent: () => import('./shared/feature-placeholder/feature-placeholder').then((module) => module.FeaturePlaceholder),
        data: {
          title: 'Salary insights',
          description: 'Explore how the organization pays people by country, department, and currency.',
        },
      },
    ],
  },
  { path: '', pathMatch: 'full', redirectTo: 'app' },
  { path: '**', redirectTo: 'app' },
];
