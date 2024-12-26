package br.com.damin.tabelaFIP.TabelaFipe.principal;

import br.com.damin.tabelaFIP.TabelaFipe.model.Dados;
import br.com.damin.tabelaFIP.TabelaFipe.model.Modelos;
import br.com.damin.tabelaFIP.TabelaFipe.model.Veiculo;
import br.com.damin.tabelaFIP.TabelaFipe.service.ConsumoApi;
import br.com.damin.tabelaFIP.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {
    private final Scanner sc = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados converteDados = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";


    public void exibirMenu(){
        var menu = """
            *** OPÇÕES ***
            Carro
            Moto
            Caminhão
            
            Digite uma das opções para consulta:
            
            """;
        String endereco;

        System.out.println(menu);
        var opcao = sc.nextLine();

        if(opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE+"carros/marcas";
        }else if(opcao.toLowerCase().contains("mot")){
            endereco = URL_BASE+"motos/marcas";
        }else{
            endereco = URL_BASE+"caminhoes/marcas";
        }
        var json = consumoApi.obterDados(endereco);

        System.out.println(json);

        var marcas = converteDados.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta:");
        var codigoMarca = sc.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var listaModelos = converteDados.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa Marca:");
        listaModelos.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


        System.out.println("\nDigite uma parte do nome do carro para ser buscado:");
        var nomeVeiculo = sc.nextLine();

        List<Dados> modelosFiltrados = listaModelos.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .toList();

        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite por favor o código do modelo");
        var codigoModelo = sc.nextLine();

        endereco += "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);
        List<Dados> anos = converteDados.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for(int i = 0; i < anos.size(); i++){
            var enderecoAnos  = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = converteDados.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }
        System.out.println("\nTodos os veiculos filtrados com avaliações por ano: "); veiculos.forEach(System.out::println);

    }
}
