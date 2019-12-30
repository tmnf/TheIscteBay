# TheIscteBay (2018)

Projeto criado para a cadeira de Programação Concorrente e Distribuida. \n\n
Programa estilo "ThePirateBay e Torrent", cada pessoa procura um ficheiro que deseje através da interface gráfica do cliente, a qual usa uma ligação com o servidor principal para verificar todos os utilizadores disponiveis. Após receber o resultado é "perguntado" a cada cliente se possui esse ficheiro e caso o utilizador deseje fazer o download do mesmo, este irá "pedir" a cada utilizador que contém o ficheiro uma parte do mesmo e coordenar a receção do mesmo.

Funcionalidades:
* Coordenação de Threads
* Partição do ficheiro em partes e envio do mesmo em bytes
* Coordenação da receção das partes do ficheiro e montagem do mesmo.
* Capacidade de lidar com utilizadores lentos e que deixam de responder.

Possui programa cliente e servidor. 
Nota máxima alcançada com este projeto.
