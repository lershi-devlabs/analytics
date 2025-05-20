"use client";
import { useEffect } from 'react';
import { inject } from '@lershi/analytics';

export function Analytics({ projectId }: { projectId: string }) {
  useEffect(() => {
    inject({ projectId });
  }, [projectId]);
  return null;
}