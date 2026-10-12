import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { WorkspaceSidebar } from '../workspace-sidebar/workspace-sidebar';

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, WorkspaceSidebar],
  templateUrl: './app-shell.html',
  styleUrl: './app-shell.scss',
})
export class AppShell {
  protected readonly sidebarCollapsed = signal(false);
}
