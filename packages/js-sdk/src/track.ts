import { getProjectId } from './project';

export async function track(eventName: string, eventData?: any) {
  const projectId = getProjectId();
  if (!projectId) throw new Error('projectId not set');
  await fetch('/api/events', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ eventName, eventData, projectId }),
  });
} 