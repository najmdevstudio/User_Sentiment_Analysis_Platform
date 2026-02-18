# 📦 **Sentiment Support AI**

### *A Multi-Agent LLM-Powered Customer Support Engine Built with Spring Boot, LangGraph Architecture & React*

---

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-orange"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.1-brightgreen"/>
  <img src="https://img.shields.io/badge/Spring_AI-1.1.2-blue"/>
  <img src="https://img.shields.io/badge/LangGraph-Node_Architecture-purple"/>
  <img src="https://img.shields.io/badge/LLM-MultiProvider-red"/>
  <img src="https://img.shields.io/badge/Grafana-Monitoring-yellow"/>
  <img src="https://img.shields.io/badge/Prometheus-Metrics-orange"/>
</p>

---

# 🧠 Overview

**Sentiment Support AI** is a full-stack, enterprise-grade **AI-driven customer support engine** that:

* Classifies user sentiment
* Generates empathetic responses
* Creates & tracks support tickets
* Answers ticket queries
* Routes messages across multiple LLM providers
* Uses a meta-agent to judge routing correctness
* Monitors everything via Prometheus + Grafana
* Stores workflow audits & execution traces in a database

It uses a **LangGraph-inspired multi-agent architecture** running on **Spring Boot**, **Java 25 virtual threads**, and **React 19** for the frontend.

This project serves as a **real blueprint for building enterprise AI orchestration systems**.

---

## Why this project exists

Most AI projects stop at proof-of-concept. This platform is intentionally designed to explore how LLM-driven workflows behave under enterprise constraints such as observability, failure handling, auditability, and long-running service stability using a JVM-first stack commonly found in production systems.

---

# 🚀 Features

### 🤖 **Multi-Agent Workflow (Node-based LangGraph)**

Agents implemented as independent nodes:

* **Classifier Agent** → Detects sentiment
* **Feedback Agent** → Handles complaints, generates tickets
* **Query Agent** → Resolves ticket status
* **Meta Agent (Judge)** → Evaluates correctness & quality

### 🧩 **Pluggable LLM Provider Router**

Supports multiple model providers:

* OpenAI
* Anthropic (Claude)
* DeepSeek
* Google Gemini
* Ollama (local LLMs)

Routing strategies:

* Single provider
* Weighted routing
* Majority vote (parallel virtual threads)
* Fallback routing
* Provider health monitoring

### 🎯 **High-quality Response Scoring**

Per message scoring metrics:

* Empathy
* Clarity
* Accuracy
* Relevance
* Routing correctness (meta-agent heuristic + LLM judge)

### 🛡️ **Resilience4j Integration**

Each agent node gets:

* Automatic retries
* Circuit breaking
* Timeout mechanisms
* Fallback execution paths

### 📊 **Observability & Monitoring**

Prometheus metrics visualized in Grafana:

* Routing success/failure
* Provider performance
* Model latency
* Workflow execution timeline
* Circuit breaker states
* Response quality (LLM judge scores)

### 🧾 **Audit Logging (DB-Persisted)**

Every workflow run is stored:

* Workflow-level entry (`workflow_execution`)
* Node-by-node execution (`workflow_step`)
* Timestamps, sentiment snapshots, agent responses

### 🖥️ **Frontend**

React 19 (Vite) web UI for interacting with the `/api/chat` endpoint.

---

# 🏛️ Architecture Diagram

```
                   ┌────────────────────────┐
                   │      ChatController     │
                   │   POST /api/chat        │
                   └─────────────┬──────────┘
                                 │
                                 ▼
                       ┌─────────────────┐
                       │ WorkflowGraph   │
                       │ (Orchestrator)  │
                       └──────┬──────────┘
                              │
     ┌────────────────────────┼────────────────────────┐
     ▼                        ▼                        ▼
┌────────────┐        ┌───────────────┐         ┌────────────────┐
│Classifier  │        │FeedbackHandler│         │QueryHandler     │
│Node        │        │Node           │         │Node             │
└────┬───────┘        └──────┬────────┘         └──────┬─────────┘
     │                       │                          │
     ▼                       ▼                          ▼
┌─────────────┐     ┌─────────────────────┐     ┌──────────────────┐
│ClassifierSvc│     │FeedbackService      │     │QueryService       │
└────┬────────┘     └──────────┬──────────┘     └────────┬─────────┘
     │                         │                         │
     ▼                         ▼                         ▼
 ┌───────────┐         ┌──────────────┐        ┌──────────────────┐
 │LLM Router │────────▶│ModelProvider*│◀──────▶│DB Repositories    │
 └───────────┘         └──────────────┘        └──────────────────┘
                              ▲
                              │
                        ┌──────────────┐
                        │Meta Agent     │
                        │LLM-as-a-Judge │
                        └──────────────┘
```

---

# 📚 Project Structure

```
sentiment-support-ai/
├── langgraph/
│   ├── WorkflowGraph.java
│   ├── nodes/
│   │    ├── ClassifierNode.java
│   │    ├── FeedbackHandlerNode.java
│   │    ├── QueryHandlerNode.java
│   │    └── Node.java
│   └── model/
│        ├── WorkflowState.java
│        ├── SentimentType.java
│
├── modules/
│   ├── classifier/ClassifierService.java
│   ├── feedback/FeedbackService.java
│   ├── query/QueryService.java
│   ├── scoring/ScoringService.java
│   ├── providers/
│   │     ├── ModelProvider.java
│   │     ├── LlmModelFactory.java
│   │     └── LlmProviderRouter.java
│
├── infrastructure/
│   ├── aop/
│   │    ├── ExecutionTraceAspect.java
│   │    └── AgentRoutingAspect.java
│   ├── metrics/
│   │    ├── WorkflowAuditService.java
│   │    └── RoutingMetricsRecorder.java
│   ├── config/
│   │    ├── SpringAIConfig.java
│   │    ├── OpenApiConfig.java
│   │    └── WebConfig.java (CORS)
│   ├── persistence/
│        ├── TicketJpaRepository.java
│        ├── WorkflowExecutionRepository.java
│        └── WorkflowStepRepository.java
│
├── api/
│   ├── ChatController.java
│   └── dto/
│        ├── ChatRequest.java
│        └── ChatResponse.java
│
├── domain/
│   ├── Ticket.java
│   ├── WorkflowExecution.java
│   └── WorkflowStep.java
│
└── frontend/ (React 19 application)
```

---

# ⚙️ Installation & Setup

## 1️⃣ Clone the repository

```bash
git clone https://github.com/<your-name>/sentiment-support-ai.git
cd sentiment-support-ai
```

---

## 2️⃣ Set environment variables

```bash
export OPENAI_API_KEY="your-key"
export ANTHROPIC_API_KEY="your-key"
export DEEPSEEK_API_KEY="your-key"
export GOOGLE_GENAI_API_KEY="your-key"
```

---

## 3️⃣ Configure database

Create PostgreSQL DB:

```sql
CREATE DATABASE sentimentdb;
CREATE USER sentiment_user WITH PASSWORD 'sentiment_pass';
GRANT ALL PRIVILEGES ON DATABASE sentimentdb TO sentiment_user;
```

`schema.sql` initializes tables automatically.

---

## 4️⃣ Start backend

```bash
mvn spring-boot:run
```

---

## 5️⃣ Start frontend (React)

```
cd frontend
npm install
npm run dev
```

---

# 🧪 Testing

Run JUnit tests:

```bash
mvn test
```

Tests include:

* Provider routing
* Majority voting logic
* Node execution
* WorkflowGraph route correctness
* Scoring JSON parsing
* Meta-agent correctness evaluation

---

# 📡 API Usage

### **POST /api/chat**

Request:

```json
{
  "message": "I want to know the status of ticket 1234"
}
```

Response:

```json
{
  "userMessage": "I want to know the status of ticket 1234",
  "sentiment": "QUERY",
  "response": "Your ticket 1234 is currently open.",
  "ticketId": "1234"
}
```

---

# 📊 Monitoring With Grafana

### Metrics exposed at:

```
/actuator/prometheus
```

### Dashboard includes:

* Routing success/failure
* Latency per node
* Response quality (empathy, clarity, accuracy)
* Provider performance
* Sentiment distribution
* Circuit breaker status
* Workflow timeline

A ready-to-import Grafana JSON dashboard is included.

---

# 🗺️ Roadmap

### 🔜 Coming soon

* Multi-turn conversational memory
* Escalation Agent (Supervisor node)
* Cost-aware provider routing
* RAG support (retrieval augmented generation)
* Knowledge graph integration (Neo4j)
* Functional multi-step workflows (LangChain-style chaining)
* Auto-fine-tuning on collected audit logs

---

# 🤝 Contributing

PRs, discussions, and feature suggestions are welcome!

---

# 📜 License

This project is licensed under the **MIT License**.
