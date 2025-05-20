# @lershi/analytics

A JavaScript/TypeScript analytics SDK for your backend.

## Install

```sh
npm install @lershi/analytics
```

## Usage

```js
import { Analytics } from '@lershi/analytics';
Analytics.init({ projectId: 'abc123' });
Analytics.track('page_view');
```

## CDN

```html
<script src="https://cdn.your-analytics.com/analytics.umd.js" data-project="abc123"></script>
<script>
  window.Analytics.track('page_view');
</script>
``` 