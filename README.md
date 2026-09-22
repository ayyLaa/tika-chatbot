# TİKAİ — TİKA Internal AI Assistant

TİKAİ is a Retrieval-Augmented Generation (RAG) based AI assistant developed for internal use within TİKA (Turkish Cooperation and Coordination Agency), Department of Information Technology (Bilgi İşlem Dairesi Başkanlığı). It allows staff to search institutional documents, regulations, organizational structure, and procedures through natural-language questions in Turkish or English.

## Purpose

TİKA is currently going through a digital transformation process. Staff access to institutional knowledge previously relied on manually searching through scattered files and web pages. TİKAİ addresses this by providing a traceable, auditable question-answering system grounded strictly in the institution's own approved sources, rather than a language model's general knowledge. If the available documents do not contain enough information to answer a question, the system says so explicitly instead of guessing.

## How It Works (Architecture)

The system consists of three main layers:

1. **Frontend (Vue.js)** — the user interface: chat screen, chat history, chat sharing, and an admin panel (user management, vector database status, risk-review queue, feedback panel, password reset approval screen).
2. **Backend (Java / Spring Boot)** — authentication (JWT, invite-based registration), authorization (role-based access: Admin/User), chat history management, keyword-based risk detection with an admin review workflow, and an admin-approved password reset process.
3. **AI Service (Python / FastAPI)** — the core of the RAG pipeline: document chunking, embedding generation, vector similarity search over PostgreSQL with pgvector, and answer generation via a language model. Built with timeout handling, retry logic, and rate-limit management for reliable operation in production.

PostgreSQL, extended with the pgvector extension, serves as the single database for both relational data (users, chat sessions, messages, feedback) and document embedding vectors.

## Technologies Used

- **Backend:** Java, Spring Boot, Spring Security (JWT)
- **AI Service:** Python, FastAPI
- **Frontend:** Vue.js, Tailwind CSS
- **Database:** PostgreSQL, pgvector
- **Infrastructure:** Docker, Git/GitHub

## Team

The project was developed by a team of two interns within TİKA's Department of Information Technology, under the guidance of institutional mentors:

- **Ajla Frkic** — majority of the backend (Java/Spring Boot) development, and the entire AI service (Python/FastAPI, RAG pipeline)
- **Safiye Alaca** — frontend (Vue.js) development, data collection and cleaning from TİKA's website, and partial contribution to the backend

## Current Status: Language Model Usage

During development and testing, Google's Gemini API is used for both text generation and embeddings, for speed and ease of integration. **This is not a permanent production decision.** Since TİKA is a public institution, sending institutional documents and staff data to a third-party cloud provider is not an acceptable long-term approach. The planned production setup is a self-hosted, open-source language model running entirely within the institution's own infrastructure, so that no institutional data ever leaves TİKA.

## Current Features

- Invite-based user registration and JWT-based authentication
- Role-based access control (Admin / User)
- Chat history and read-only chat sharing with colleagues
- Keyword-based risk detection: suspicious questions are automatically flagged and routed to an admin for review
- Admin-approved password reset workflow: no reset e-mail is sent without explicit administrator approval
- User feedback (like/dislike) with admin panel review
- Vector database status monitoring (indexed, pending, and failed documents)

## Future Plans

- **Fine-tuning and model training:** a language model fine-tuned on the institution's own data, tailored specifically to TİKA
- **Expanding the dataset:** incorporating more institutional documents, regulations, and approved external sources
- **Multi-agent architecture:** specialized agents coordinating on different tasks (document search, form assistance, process guidance)
- **Mobile application:** access to TİKAİ from mobile devices
- **AI-powered analytics and predictive risk detection:** analyzing usage patterns and moving risk detection beyond the current keyword-based approach toward a machine learning classifier (see [query-risk-classifier](https://github.com/ayyLaa/query-risk-classifier)), so that suspicious questions can be detected proactively based on learned patterns rather than fixed keywords
- **Document encryption:** encrypting the source documents used by the RAG system at rest, adding an additional layer of protection

## Security Note

Since this project is intended for use within a public institution's internal systems, security has been a central design concern from the start: sensitive actions (password resets, user invitations) require human approval, suspicious usage is automatically flagged and reviewed, and credentials are managed through environment variables rather than being hard-coded into the codebase.