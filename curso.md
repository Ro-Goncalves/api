
## Boas Práticas API

Em DELETE podemos retornar 204 (No Content) se o recurso foi excluído com sucesso

@DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);
        medico.excluir();

        return ResponseEntity.noContent().build();
    }

Todos os métodos irão retornar ResponseEntity

@GetMapping("")
public ResponseEntity<Page<DadosListagemMedico>> buscarTodos(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
    var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemMedico::new);
    return ResponseEntity.ok(page);
}

@PutMapping("")
@Transactional
public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizacaoMedico dados) {
    var medico = repository.getReferenceById(dados.id());
    medico.atualizarInformacoes(dados);

    return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
}

O POST retorna 201 

@PostMapping("")
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroMedico dados, UriComponentsBuilder uriBuilder) {        
        var medico =  new Medico(
                null, 
                Boolean.TRUE,
                dados.nome(), 
                dados.email(), 
                dados.crm(),
                dados.telefone(), 
                dados.especialidade(), 
                new Endereco(
                    dados.endereco().logradouro(), 
                    dados.endereco().bairro(), 
                    dados.endereco().cep(),
                    dados.endereco().cidade(), 
                    dados.endereco().uf(),
                    dados.endereco().complemento(),
                    dados.endereco().numero()
                )
            );
        
        repository.save(medico);

        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoMedico(medico));
    }

Nova URI
 Método para detalhar o médico.
 @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);       

        return ResponseEntity.ok(new DadosDetalhamentoMedico(medico));
    }

## Lidando com erros

Movendo classes para o "Dominio"

pripriedade para não retornar stacktrace na API server.error.include-stacktrace=never

Classe para tratar erros
@RestControllerAdvice
public class TratadorErros {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarEntityNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNoValidExcpetion.class)
    public ResponseEntity<Void> tratarMethodArgumentNoValidExcpetion(MethodArgumentNoValidExcpetion exception) {
        var erros = exception.getFieldErrors();
        return ResponseEntity.badRequest().body(
            erros.strem()
                .map(DadosErroValidacao::new)
                .toList()
        );
    }

    private Record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError error) {
            this(
                erro.getField(),
                erro.getDefaultMessage()
            )
        }
    }
}

Centralizando mensagens de erro com arquivo ValidationMessagens

nome.obrigatorio=Nome é obrigatório
email.obrigatorio=Email é obrigatório
email.invalido=Formato do email é inválido
telefone.obrigatorio=Telefone é obrigatório
crm.obrigatorio=CRM é obrigatório
crm.invalido=Formato do CRM é inválido
especialidade.obrigatoria=Especialidade é obrigatória
endereco.obrigatorio=Dados do endereço são obrigatórios
id.obrigatorio=O ID deve ser informado

a classe fica assim

public record DadosAtualizacaoMedico(
    
    @NotNull(message = "{id.obrigatorio}")
    Long id,

    String nome,
    String telefone,
    DadosEndereco endereco
) {}

## Spring Security