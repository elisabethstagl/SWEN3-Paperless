FROM node:lts-slim AS build
WORKDIR /src
RUN npm install -g @angular/cli
COPY . ./
RUN npm ci
RUN ng build

FROM nginx:stable AS final
EXPOSE 80
COPY --from=build src/dist/frontend-paperless/browser  /usr/share/nginx/html
