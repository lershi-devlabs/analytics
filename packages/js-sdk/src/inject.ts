import { setProjectId } from './project';
import { startSession, pageView, endSession } from './session';

export function inject({ projectId }: { projectId: string }) {
  setProjectId(projectId);
  startSession();
  pageView();
  window.addEventListener('beforeunload', endSession);
} 