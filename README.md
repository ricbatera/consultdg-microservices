# ConsultDG Microservices Suite

Este projeto reúne a suíte de micro serviços da ConsultDG, projetada para oferecer soluções escaláveis, modulares e de alta performance. Cada serviço é responsável por uma funcionalidade específica, facilitando a manutenção, o desenvolvimento independente e a integração contínua. O objetivo é proporcionar uma arquitetura robusta, flexível e alinhada com as melhores práticas do mercado para atender às demandas da ConsultDG.

## Microserviços Disponíveis

- **api-boleto-service**: Responsável por gerenciar as requisições de entrada do SISTEMA DE BOELTOS e orquestrar a comunicação entre os demais microserviços.
- **s3-service**: Gerencia o armazenamento e a recuperação de arquivos utilizando a integração com o Amazon S3, garantindo segurança e alta disponibilidade dos dados.
- **protocolo-service**: Responsável por realizar o acompanhamento fim-a-fim dos fluxos das requisições, registrando cada etapa percorrida até a conclusão. Todos os demais microserviços se conectam a este serviço para registrar suas atividades, garantindo rastreabilidade, auditoria e visibilidade completa do ciclo de vida das operações.

## Serviços de Apoio

- **protocolo-service**: Responsável por gerenciar o estado do fluxo dos processos, acompanhando cada etapa até a sua conclusão.
- **Gerenciamento de Configurações**: O projeto utiliza o **Spring Cloud Config Server** para centralizar e gerenciar os arquivos `application.properties` de todos os microserviços, facilitando a manutenção e a atualização das configurações de forma segura e eficiente.

## Bibliotecas de Apoio para Contratos de Comunicação

Para padronizar e facilitar a integração entre os microserviços, o projeto conta com bibliotecas compartilhadas que centralizam contratos, modelos e utilitários:

- **database-mysql-service**: Biblioteca que reúne todos os repositórios e modelos necessários para comunicação com bancos de dados MySQL. Permite que diferentes microserviços reutilizem a mesma implementação de acesso a dados, promovendo consistência e reduzindo duplicidade de código.

- **protocolo-service-util**: Biblioteca que disponibiliza todos os DTOs (Data Transfer Objects) utilizados na integração entre os microserviços e o protocolo-service. Garante que todos os serviços sigam o mesmo padrão de comunicação ao registrar e consultar informações de protocolo, facilitando a interoperabilidade e a manutenção.