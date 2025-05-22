"use client";
import { useEffect } from 'react';
import { inject, pageView, endSession, trackClick } from 'lershi-analytics';

export function Analytics({ projectId }: { projectId: string}) {
  useEffect(() => {
    inject({ projectId });
    // Track initial pageview
    if (typeof window !== 'undefined') {
      pageView();

      // End session on unload
      const handleUnload = () => {
        endSession();
      };
      window.addEventListener('beforeunload', handleUnload);

      // Track internal/external link clicks
      const handleClick = (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (target && target.tagName === 'A') {
          const anchor = target as HTMLAnchorElement;
          if (anchor.href) {
            const isInternal = anchor.href.includes(window.location.host);
            trackClick({
              projectId,
              url: anchor.href,
              type: isInternal ? 'internal' : 'external',
              // sessionId: getSessionId(),
            });
          }
        }
      };
      document.addEventListener('click', handleClick);

      return () => {
        window.removeEventListener('beforeunload', handleUnload);
        document.removeEventListener('click', handleClick);
      };
    }
    return () => {};
  }, [projectId]);

  return null;
}