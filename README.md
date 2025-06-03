# Locadora Filmes

Sistema simples para gerenciamento de locações de filmes, desenvolvido em Java com banco de dados SQLite.

Este projeto oferece funcionalidades básicas para uma locadora de filmes, permitindo:

- Cadastro, listagem, atualização e exclusão de filmes
- Cadastro e consulta de clientes
- Registro de locações com controle de estoque e prazo de devolução
- Registro e controle de devoluções
- Visualização das locações ativas e históricas

O intuito principal deste projeto foi implementar testes unitários abrangentes para garantir a qualidade do código, bem como realizar a integração completa com o banco de dados SQLite usando JDBC. O acesso ao banco é feito de forma segura e sincronizada para evitar problemas de concorrência.

O sistema é ideal para fins educacionais, servindo como base para o desenvolvimento de sistemas de locação mais robustos.

---

## Tecnologias utilizadas

- Java 17+
- SQLite (via JDBC)
- JUnit 5 para testes unitários
- Maven para gerenciamento de dependências e build

---

## Como executar

1. Clone este repositório
2. Importe o projeto em sua IDE favorita (Eclipse, IntelliJ, VSCode)
3. Execute os testes unitários para verificar o funcionamento (`mvn test`)
4. Execute a aplicação principal (ex: `Main.java`) para usar o sistema

---

## Estrutura do projeto

- `src/main/java`: código fonte da aplicação
- `src/test/java`: testes unitários
- `locadora.db`: arquivo do banco de dados SQLite
- `.gitignore`: configurações para ignorar arquivos temporários e builds
- `pom.xml`: arquivo de configuração do Maven

---




