import { writeFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { spawnSync } from 'node:child_process';

const apiBaseUrl = process.env.API_BASE_URL;

if (!apiBaseUrl || !URL.canParse(apiBaseUrl) || !apiBaseUrl.startsWith('https://')) {
  throw new Error('API_BASE_URL must be the public HTTPS URL of the deployed API.');
}

const configPath = resolve('src/app/core/api/api.config.ts');
writeFileSync(configPath, `export const API_BASE_URL = ${JSON.stringify(apiBaseUrl)};\n`);

const npm = process.platform === 'win32' ? 'npm.cmd' : 'npm';
const build = spawnSync(npm, ['run', 'build'], { stdio: 'inherit' });

if (build.error) {
  throw build.error;
}

process.exit(build.status ?? 1);
