import { getProjectId } from './project';

let sessionId: string | undefined;
let apiUrl = 'https://analytics-production-df31.up.railway.app';

export function configure(options: { apiUrl: string }) {
  apiUrl = options.apiUrl;
}

export async function startSession() {
  const projectId = getProjectId();
  if (!projectId) return;
  const res = await fetch(`${apiUrl}/api/sessions/start`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      projectId,
      userAgent: navigator.userAgent,
      entryPage: window.location.pathname,
      referrer: document.referrer || null,
    }),
  });
  const data = await res.json();
  sessionId = data.sessionId;
}

export async function pageView() {
  const projectId = getProjectId();
  if (!projectId || !sessionId) return;
  await fetch(`${apiUrl}/api/sessions/pageview`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      projectId,
      sessionId,
      pageUrl: window.location.pathname,
    }),
  });
}

export async function endSession() {
  const projectId = getProjectId();
  if (!projectId || !sessionId) return;
  await fetch(`${apiUrl}/api/sessions/end`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      projectId,
      sessionId,
      lastPageUrl: window.location.pathname,
    }),
  });
}

export async function trackClick({
  projectId,
  url,
  type,
  sessionId,
}: {
  projectId: string;
  url: string;
  type: 'internal' | 'external';
  sessionId?: string;
}) {
  await fetch(`${apiUrl}/api/sessions/click`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      projectId,
      url,
      type,
      sessionId,
    }),
  });
} 