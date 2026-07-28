# Wilkystorm

Wilkystorm is a modern personal website and backend microservice. It preserves the original site's identity, profile links, and personal message while replacing the old AngularJS/Tomcat WAR and binary-only quote service with a Spring Boot application.

The app renders a responsive server-side UI and exposes a small JSON API for reading the current Fred Rogers quote. Grok is called only by the backend on startup and by a scheduled daily refresh, never by browser code.

## Architecture

- Spring Boot 3.3.4, Java 21
- Thymeleaf server-rendered UI
- Spring MVC JSON API
- Scheduled in-memory daily quote
- Spring Boot Actuator health endpoint
- Docker multi-stage image build
- Single-replica Kubernetes Deployment and public Network Load Balancer Service

## Local Development

Run the app:

```bash
./gradlew bootRun
```

Open:

```text
http://localhost:8080
```

API:

```text
GET /api/quote
GET /actuator/health
```

## Configuration

Set these outside source control:

```text
XAI_API_KEY=...
XAI_API_URL=https://api.x.ai/v1
XAI_MODEL=grok-4.5
```

`XAI_API_KEY` is the primary API-key variable, matching the `sdc-rtc` xAI convention. `GROK_API_KEY` is also supported as a backward-compatible fallback. When neither key is present or the Grok request fails and no previous successful quote is available, the backend returns a safe fallback quote.

The frontend does not receive the API key. Browser refreshes and `GET /api/quote` return the stored quote and do not call Grok.

## Grok Integration

The backend sends a chat completion request to:

```text
POST ${XAI_API_URL}/chat/completions
```

The prompt asks for exactly one meaningful and uplifting Fred Rogers quote and instructs the model to return only the quote text. The service loads one quote when the application starts and refreshes it once daily at 6:00 AM America/New_York.

The current quote is stored in memory. If the scheduled Grok request returns a fallback response or fails, Wilkystorm keeps the last successful quote when possible. If no successful quote exists yet, it uses the safe fallback quote.

## Build And Test

```bash
./gradlew test
./gradlew bootJar
```

The project follows the same Gradle/Spring Boot pattern as `sdc-rtc`.

## Container Image

```bash
docker build -t wilkystorm:latest .
```

## Kubernetes

Resource names:

- Deployment: `wilkystorm`
- Replicas: `1`
- Service: `wilkystorm`
- Service type: `LoadBalancer`
- Container: `wilkystorm`
- Shared Secret reference: `xai-api-secret`
- Shared Secret key: `XAI_API_KEY`
- App label and selector: `app: wilkystorm`
- EKS cluster: `wilkystorm`
- Namespace: `default`
- ECR repository: `wilkystorm`
- IAM deployment role: `wilkystorm-deploy-role`

The Service selector is intentionally unique:

```yaml
selector:
  app: wilkystorm
```

That prevents the Wilkystorm Service from selecting `sdc-rtc` pods and prevents `sdc-rtc` Services from selecting Wilkystorm pods.

Wilkystorm reads the same existing Kubernetes Secret used by `sdc-rtc`: `xai-api-secret` with key `XAI_API_KEY` in the `default` namespace. This repository does not own, create, or update that Secret.

The Deployment intentionally runs one replica so all users see the same in-memory daily quote. Running multiple replicas without shared quote persistence could show different quotes from different pods.

The single `wilkystorm` Service is type `LoadBalancer`. Kubernetes still assigns it an internal ClusterIP, and EKS provisions a public internet-facing AWS Network Load Balancer for external traffic. The existing `sdc-rtc` load balancer is separate and is not shared or modified.

HTTPS uses the ACM certificate for:

- `wilkystorm.com`
- `www.wilkystorm.com`

After AWS assigns the Wilkystorm LoadBalancer hostname, configure GoDaddy DNS so `www.wilkystorm.com` is a CNAME pointing to that hostname. Root-domain DNS for `wilkystorm.com` is handled separately.

Validate manifests:

```bash
kubectl apply --dry-run=client -f k8s/deployment.yaml
kubectl apply --dry-run=client -f k8s/service.yaml
```

Deploy:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl rollout status deployment/wilkystorm --timeout=180s
```

## GitHub Actions

`.github/workflows/deploy.yml` runs only for pull request events targeting `master`.

When a pull request is opened, synchronized, or reopened, the workflow runs the small focused test suite and builds the application with `./gradlew test bootJar`. Changes pushed to an open pull request trigger another validation build. The workflow does not authenticate to AWS, build a Docker image, push to ECR, or deploy during these validation events.

Deployment runs only when a pull request is closed as successfully merged into `master`. The deployment job checks out the exact merge commit, builds the merged commit with `./gradlew bootJar` without repeating tests, assumes `arn:aws:iam::792028225466:role/wilkystorm-deploy-role` through GitHub OIDC, builds the Wilkystorm image, tags it with the merge commit SHA, pushes it to `792028225466.dkr.ecr.us-east-1.amazonaws.com/wilkystorm`, applies only the Wilkystorm manifests, and updates only `deployment/wilkystorm` container `wilkystorm` in the `default` namespace.

Direct pushes to any branch, including `master`, do not trigger this workflow. Closing a pull request without merging does not build or deploy. Because the workflow no longer listens for `push` events, a pull request merge should produce only one workflow run for the merge event.

The workflow deploys the single Wilkystorm LoadBalancer Service. AWS provisions a Wilkystorm-specific internet-facing Network Load Balancer; DNS is configured after the LoadBalancer hostname is available.

Do not commit AWS credentials, Grok keys, generated kubeconfigs, or local `.env` files.
