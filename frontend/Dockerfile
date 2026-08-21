# Build stage
FROM node:20-alpine AS builder

WORKDIR /build

COPY package.json ./
RUN npm install

COPY angular.json tsconfig.json tsconfig.app.json ./
COPY src src

RUN npm run build

# Runtime stage
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /build/dist/srmasset-web/browser /usr/share/nginx/html

EXPOSE 80
