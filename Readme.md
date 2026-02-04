PDV Restaurante / Bar - API MVP

API desenvolvida em Spring Boot para o MVP de um sistema de PDV (Ponto de Venda) focado em restaurantes e bares.  
O MVP permite que garçons criem pedidos usando NFC para identificar mesas e adicionem produtos aos pedidos.

-------------------------------------------------------
Tecnologias

- Backend: Java Spring Boot
- Banco de Dados: PostgreSQL
- Autenticação: JWT
- Persistência: JPA / Hibernate

-------------------------------------------------------
Estrutura do Banco de Dados

cards
- id UUID (PK)
- uid UUID UNIQUE NOT NULL
- active BOOLEAN DEFAULT true
- created_at TIMESTAMP
  Relação: 1 card → 0..1 tables

tables
- id UUID (PK)
- number INT NOT NULL
- card_id UUID UNIQUE NULL (FK → cards.id)
- status VARCHAR(20) — OPEN | CLOSED
- created_at TIMESTAMP
  Relações:
- 1 mesa → 0..1 card
- 1 mesa → N pedidos (orders)

products
- id UUID (PK)
- name VARCHAR(100) NOT NULL
- price NUMERIC(10,2) NOT NULL
- active BOOLEAN DEFAULT true
- created_at TIMESTAMP
  Relação: 1 produto → N order_items

orders
- id UUID (PK)
- table_id UUID NOT NULL (FK → tables.id)
- status VARCHAR(20) — OPEN | CLOSED | CANCELED
- created_at TIMESTAMP
  Relações:
- 1 pedido → 1 mesa
- 1 pedido → N order_items

order_items
- id UUID (PK)
- order_id UUID NOT NULL (FK → orders.id)
- product_id UUID NOT NULL (FK → products.id)
- quantity INT NOT NULL
- unit_price NUMERIC(10,2) NOT NULL
  Relações:
- N order_items → 1 pedido
- N order_items → 1 produto

users
- id UUID (PK)
- name VARCHAR(100) NOT NULL
- email VARCHAR(100) UNIQUE NOT NULL
- password VARCHAR(255) NOT NULL
- role VARCHAR(20) — WAITER
- active BOOLEAN DEFAULT true
  Relação: nenhuma no MVP

-------------------------------------------------------
EndPoints da API

Auth
POST /auth/login
- Autentica garçom e retorna JWT.

Tables
GET /tables/by-card/{cardUid}
- Busca mesa pelo NFC (cardUid)
- Retorna dados da mesa e pedido OPEN (se existir)

Products
GET /products
- Lista produtos ativos para criar pedidos

Orders
POST /orders
- Cria um pedido OPEN para a mesa

GET /orders/{orderId}
- Retorna pedido + itens

POST /orders/{orderId}/items
- Adiciona item ao pedido (produto + quantidade)

PUT /orders/{orderId}/items/{itemId}
- Atualiza quantidade do item

DELETE /orders/{orderId}/items/{itemId}
- Remove item do pedido

-------------------------------------------------------
Fluxo de Uso do MVP

1. Garçom faz login → recebe token JWT
2. Garçom lê NFC da mesa → backend retorna mesa + pedido OPEN
3. Garçom lista produtos → seleciona itens
4. Itens são adicionados ao pedido (order_items)
5. Pedido permanece OPEN (fechamento e pagamento não implementados no MVP)

-------------------------------------------------------
Observações

- Cada mesa pode ter apenas um pedido OPEN por vez
- Cada card só pode estar vinculado a uma mesa por vez
- O preço do item é copiado do produto no momento do pedido
- Pagamentos, fechamento de mesa e relatórios ficam para versões futuras

-------------------------------------------------------
Como Rodar

1. Configurar PostgreSQL
2. Criar banco de dados e rodar migrations
3. Configurar application.properties com URL, usuário e senha do banco
4. Build do Spring Boot:
   ./mvnw clean install
   ./mvnw spring-boot:run
5. API disponível em http://localhost:8080

-------------------------------------------------------
MVP Foco

- Funcionamento básico do PDV
- Integração NFC → mesa → pedido
- Adicionar produtos aos pedidos
- Backend consistente, pronto para evoluir
