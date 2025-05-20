import { getProjectId } from './project';

let sessionId: string | undefined;

export async function startSession() {
  const projectId = getProjectId();
  if (!projectId) return;
  const res = await fetch('/api/sessions/start', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      projectId,
      userAgent: navigator.userAgent,
      referrer: document.referrer,
    }),
  });
  const data = await res.json();
  sessionId = data.sessionId;
}

export async function pageView() {
  const projectId = getProjectId();
  if (!projectId || !sessionId) return;
  await fetch('/api/sessions/pageview', {
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
  await fetch('/api/sessions/end', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      projectId,
      sessionId,
      lastPageUrl: window.location.pathname,
    }),
  });
} 