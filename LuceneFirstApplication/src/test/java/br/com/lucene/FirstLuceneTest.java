package br.com.lucene;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;

public class FirstLuceneTest {

	@Test
	@Ignore
	public void luceneTest() throws IOException, ParseException {
		Analyzer analyzer = new StandardAnalyzer();
		Directory directory = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		
		Document doc = new Document();
		String text = "Text de teste feito apenas para aprender mais sobre lucene.";
		doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
		iwriter.addDocument(doc);
		iwriter.close();
		
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		QueryParser parser = new QueryParser("fieldname", analyzer);
		
		//em parse vai texto a ser pesquisado no documento indexado
		Query query = parser.parse("text");
		ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
		TestCase.assertEquals(1, hits.length);
		
		for (int i = 0; i < hits.length; i++) {
			Document hitDoc = isearcher.doc(hits[i].doc);
			System.out.println(hitDoc.get("fieldname"));
		}
		
		ireader.close();
		directory.close();
	}
	
	@Test
	public void luceneTest2() throws IOException, ParseException {
		Analyzer analyzer = new StandardAnalyzer();
		Directory directory = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		
		StringBuilder menu = new StringBuilder("O que você deseja fazer?\n");
		menu.append("1 - Criar novo documento e inserir dados nele\n");
		menu.append("2 - Fazer uma pesquisa\n");
		menu.append("3 - Sair\n");
		
		Boolean finalizar = false;
		while(!finalizar) {
			Integer escolha = validInteger(JOptionPane.showInputDialog(menu.toString()));
			
			if(escolha == 1) {
				IndexWriter iwriter = new IndexWriter(directory, config);
				Boolean finalizarCriacao = false;
				do {
					iwriter.addDocument(criarDocumento());
					Integer tempEscolha = 
							validInteger(JOptionPane.showInputDialog("Deseja continuar criando documentos?"
									+ "\n1 - sim\n2 - não"));
					if(tempEscolha == 2)
						finalizarCriacao = true;
				}while(!finalizarCriacao);
				iwriter.close();
			}
			else if(escolha == 2) {
				DirectoryReader ireader = DirectoryReader.open(directory);
				IndexSearcher isearcher = new IndexSearcher(ireader);
				QueryParser parser = new QueryParser("fieldname", analyzer);
				
				Boolean finalizarPesquisa = false;
				do {
					String textSearch = JOptionPane.showInputDialog("Digite o que você deseja pesquisar: ");
					Query query = parser.parse(textSearch);
					ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
					TestCase.assertEquals(1, hits.length);
					
					StringBuilder result = new StringBuilder("Resultado:\n\n");
					for (int i = 0; i < hits.length; i++) {
						Document hitDoc = isearcher.doc(hits[i].doc);
						result.append(hitDoc.get("fieldname")).append("\n");
					}
					System.out.println(result.toString());
					
					Integer tmpEscolha = validInteger(JOptionPane.showInputDialog(null, "1 - nova pesquisa\n2-sair"));
					finalizarPesquisa = tmpEscolha == 1 ? true : false;
				}while(!finalizarPesquisa);
				ireader.close();
			}
			else if(escolha == 3) {
				finalizar = true;
			}
			else {
				JOptionPane.showMessageDialog(null, "Escolha inválida");
			}
		}
		directory.close();
	}
	
	private Document criarDocumento() {
		StringBuilder menu = new StringBuilder("O que você deseja fazer?\n");
		menu.append("1 - Adicionar novo conteúdo ao documento\n");
		menu.append("2 - Salvar o documento e sair");
		
		Document doc = new Document();
		Boolean fianlizar = false;
		while(!fianlizar) {
			Integer escolha = validInteger(JOptionPane.showInputDialog(menu.toString()));
			if(escolha == 1) {
				String text = JOptionPane.showInputDialog("Digite o novo conteúdo do documento aqui:");
				doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
			}
			else if(escolha == 2) {
				fianlizar = true;
			}
		}
		return doc;
	}
	
	private Integer validInteger(Object obj) {
		if(obj != null && !obj.toString().equals(""))
			return Integer.valueOf(obj.toString());
		return 0;
	}
	
}
