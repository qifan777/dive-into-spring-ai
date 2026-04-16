# Front-End Demo

## Overview

This directory contains the Vue 3 + Vite frontend used by the Spring AI teaching demos. It is responsible for login, chat-style interaction, generated API bindings, and the main demo pages under `src/views`.

## Prerequisites

- Node.js 18+
- npm
- a running backend service from the repository root

## Setup

```sh
npm install
```

If you want to customize local addresses first, copy `front-end/.env.example` to `.env.development` and adjust the values for your environment.

## Run

Generate the frontend API bindings after the backend is available:

```sh
npm run api
```

Then start the local dev server:

```sh
npm run dev
```

## Expected Behavior

- the Vite development server starts without missing dependency errors
- the generated API files are refreshed after `npm run api`
- the frontend can reach the backend through the configured local address

## Useful Commands

```sh
npm run build
npm run lint
```
