declare global {
  interface Window {
    Analytics: any;
  }
}

import { inject } from '@lershi/analytics';
import { track } from '@lershi/analytics';

window.Analytics = {
  inject,
  track,
};