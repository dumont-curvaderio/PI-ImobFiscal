# Como Rodar o ImobFiscal

**PI 2 · FATEC DSM 2026-2**

Este guia cobre duas situações:
- **Parte 1** — rodar localmente (para desenvolvimento e apresentação)
- **Parte 2** — publicar na internet (deploy para deixar online)

---

## Parte 1 — Rodar Localmente

### O que você precisa instalar (uma vez só)

| Ferramenta | Versão | Para quê |
|---|---|---|
| Java JDK 17 | 17 ou superior | Rodar o backend Spring Boot |
| Apache Maven | 3.9+ | Compilar e executar o backend |
| PostgreSQL | 15 ou 16 | Banco de dados local |
| Node.js | já instalado ✅ | Rodar o frontend React |

---

### Passo 1 — Instalar o Java JDK 17

1. Acesse **adoptium.net** no seu navegador
2. Clique em **Latest LTS** → escolha **Temurin 21** (compatível com o projeto)
3. Baixe o instalador `.msi` para Windows
4. Execute o instalador e marque a opção **"Set JAVA_HOME variable"**
5. Feche e reabra o terminal após instalar

**Verificar se funcionou:**
```powershell
java -version
# Deve aparecer: openjdk version "21..." ou "17..."
```

---

### Passo 2 — Instalar o Apache Maven

1. Acesse **maven.apache.org/download.cgi**
2. Baixe o arquivo **Binary zip archive** (`apache-maven-3.9.x-bin.zip`)
3. Extraia o zip para `C:\Program Files\Maven\`
4. Adicione o Maven ao PATH do Windows:
   - Abra **Variáveis de Ambiente** (pesquise no Windows)
   - Em **Variáveis do Sistema** → PATH → **Editar** → **Novo**
   - Adicione: `C:\Program Files\Maven\apache-maven-3.9.x\bin`
5. Feche e reabra o terminal

**Verificar se funcionou:**
```powershell
mvn -version
# Deve aparecer: Apache Maven 3.9.x
```

---

### Passo 3 — Instalar o PostgreSQL

1. Acesse **postgresql.org/download/windows/**
2. Clique em **Download the installer** → baixe a versão 16
3. Execute o instalador com as opções padrão
4. Na tela de senha, coloque: **`postgres`** (exatamente assim, sem aspas)
5. Porta padrão: **5432** (não mudar)
6. Conclua a instalação

**Verificar se funcionou:**
```powershell
psql -U postgres -c "\l"
# Vai pedir a senha (postgres) e mostrar a lista de bancos
```

---

### Passo 4 — Criar o banco de dados

Abra o terminal **PowerShell** na pasta do projeto:

```powershell
# Entrar na pasta do projeto
cd "c:\Users\chris\OneDrive\FATEC\Fontes\PI - ContabilFiscal\imobfiscal"

# Criar o banco de dados
psql -U postgres -c "CREATE DATABASE imobfiscal;"

# 1. Estrutura base (tabelas, constraints, índices)
psql -U postgres -d imobfiscal -f database\schema.sql

# 2. Motor fiscal (tabelas de IBS/CBS e alíquotas)
psql -U postgres -d imobfiscal -f database\V2__motor_fiscal.sql

# 3. Dados de exemplo (imóveis, locadores, contrato de demo)
psql -U postgres -d imobfiscal -f database\seed.sql
```

Execute os scripts **nessa ordem** — cada um depende do anterior.
Cada comando vai pedir a senha do PostgreSQL: **postgres**

> **Nota:** o schema não é criado automaticamente pelo backend.
> Sempre que recriar o banco, rode os três scripts acima na sequência.

**Verificar se funcionou:**
```powershell
psql -U postgres -d imobfiscal -c "SELECT * FROM imobiliarias;"
# Deve mostrar uma linha: "ImobFiscal Demo"
```

---

### Passo 5 — Iniciar o Backend (Spring Boot)

Abra um terminal **novo** (deixe este aberto enquanto usa o sistema):

```powershell
cd "c:\Users\chris\OneDrive\FATEC\Fontes\PI - ContabilFiscal\imobfiscal\backend"

mvn spring-boot:run
```

Na primeira execução o Maven vai baixar as dependências — pode demorar 2 a 5 minutos.

**O backend está pronto quando aparecer no terminal:**
```
Started ImobfiscalApplication in X.XXX seconds
```

**Verificar se funcionou** (abra o navegador):
```
http://localhost:8080/api/auth/ping
```
Ou teste com PowerShell:
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -Method GET
```

---

### Passo 6 — Iniciar o Frontend (React + Vite)

Abra um **segundo terminal** (backend deve continuar rodando no primeiro):

```powershell
cd "c:\Users\chris\OneDrive\FATEC\Fontes\PI - ContabilFiscal\imobfiscal\frontend"

# Instalar dependências (só precisa fazer na primeira vez)
npm install

# Iniciar o servidor de desenvolvimento
npm run dev
```

**Abra no navegador:**
```
http://localhost:5173
```

---

### Credenciais de acesso (dados do seed.sql)

| Campo | Valor |
|---|---|
| E-mail | `admin@imobfiscal.com.br` |
| Senha | `admin123` |

Ou cadastre um novo usuário clicando em **"Criar conta"** na tela de login.

---

### Resumo rápido (depois de tudo instalado)

Toda vez que quiser rodar o sistema, abra **dois terminais**:

**Terminal 1 — Backend:**
```powershell
cd "c:\...\imobfiscal\backend"
mvn spring-boot:run
```

**Terminal 2 — Frontend:**
```powershell
cd "c:\...\imobfiscal\frontend"
npm run dev
```

Acesse: **http://localhost:5173**

---

### Solução de problemas comuns

| Problema | Causa provável | Solução |
|---|---|---|
| `java: command not found` | Java não instalado | Seguir Passo 1 e reiniciar o terminal |
| `mvn: command not found` | Maven não instalado ou não no PATH | Seguir Passo 2 e reiniciar o terminal |
| `psql: command not found` | PostgreSQL não está no PATH | Adicionar `C:\Program Files\PostgreSQL\16\bin` ao PATH |
| `Connection refused` ao rodar o backend | PostgreSQL não está rodando | Abrir **Serviços** do Windows e iniciar `postgresql-x64-16` |
| `FATAL: role "postgres" does not exist` | Senha incorreta | Reinstalar PostgreSQL com senha `postgres` |
| Frontend mostra tela em branco | Backend não está rodando | Iniciar o backend (Terminal 1) antes do frontend |
| Erro 401 ao logar | Banco sem seed | Rodar `seed.sql` novamente (Passo 4) |

---

## Parte 2 — Deploy (Publicar na Internet)

> Esta parte é opcional para o PI — o sistema pode ser apresentado rodando localmente.
> Se quiser deixar online, siga os passos abaixo.

### Visão geral

```
[Usuário] → Vercel (frontend) → Render.com (backend) → Supabase (banco)
```

Todos os serviços têm plano **gratuito**.

---

### 2.1 — Banco de dados no Supabase

1. Acesse **supabase.com** e crie uma conta gratuita
2. Clique em **New Project**
   - Nome: `imobfiscal`
   - Senha do banco: anote em lugar seguro
   - Região: **South America (São Paulo)**
3. Aguarde o projeto ser criado (1-2 min)
4. Vá em **SQL Editor** (menu lateral) e execute o conteúdo de `database/schema.sql`
5. Execute o conteúdo de `database/seed.sql`
6. Vá em **Project Settings → Database** e copie a **Connection string** (URI):
   ```
   postgresql://postgres:[SENHA]@db.[ID].supabase.co:5432/postgres
   ```

---

### 2.2 — Backend no Render.com

1. Acesse **render.com** e crie uma conta gratuita
2. Clique em **New → Web Service**
3. Conecte ao seu repositório GitHub (`PI-ImobFiscal`)
4. Configurações:
   - **Root Directory:** `backend`
   - **Runtime:** Java
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -jar target/imobfiscal-backend-1.0.0.jar`
5. Em **Environment Variables**, adicione:

| Variável | Valor |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://db.[ID].supabase.co:5432/postgres` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | senha do Supabase |
| `CORS_ORIGINS` | URL do Vercel (preencher depois de criar o frontend) |

> **Não existe `JWT_SECRET`** — a API é aberta. O login confere e-mail/senha via BCrypt
> e devolve um marcador de sessão simples (não um JWT).

6. Clique em **Create Web Service**
7. Aguarde o deploy (3-5 min) e copie a URL gerada: `https://imobfiscal-backend.onrender.com`

---

### 2.3 — Frontend no Vercel

1. Acesse **vercel.com** e crie uma conta gratuita
2. Clique em **Add New → Project**
3. Importe o repositório `PI-ImobFiscal` do GitHub
4. Configurações:
   - **Root Directory:** `frontend`
   - **Framework Preset:** Vite
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
5. Em **Environment Variables**, adicione:

| Variável | Valor |
|---|---|
| `VITE_API_URL` | `https://imobfiscal-backend.onrender.com/api` |

6. Clique em **Deploy**
7. Após o deploy, copie a URL: `https://imobfiscal.vercel.app`
8. **Volte ao Render.com** e atualize a variável `CORS_ORIGINS` com a URL do Vercel

---

### 2.4 — Confirmar que o deploy funcionou

1. Acesse a URL do Vercel no navegador
2. Faça login com `admin@imobfiscal.com.br` / `admin123`
3. Verifique se os imóveis de demo aparecem na listagem

---

### Observação sobre o plano gratuito do Render.com

O plano gratuito do Render.com **hiberna** a aplicação após 15 minutos sem uso.
Na primeira requisição depois da hibernação, o backend pode demorar **30-60 segundos** para responder.
Isso é normal para o plano gratuito — o plano pago elimina esse comportamento.

Para a apresentação do PI, se for usar o deploy, acesse o sistema 2 minutos antes da banca
para garantir que o backend já "acordou".

---

*ImobFiscal · PI 2 · FATEC Indaiatuba 2026*
