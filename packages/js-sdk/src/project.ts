let _projectId: string | undefined;

export function setProjectId(id: string) {
  _projectId = id;
}

export function getProjectId() {
  return _projectId;
} 