package com.leonardo.flappy.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class TelaPrincipal extends ApplicationAdapter {

	//Texturas
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo, canoEmCima;
	private Texture gameOver;

	//Formas para colisão
	private ShapeRenderer shapeRenderer;
	 private Circle circlePassaro;
	 private Rectangle rectangleCanoCima;
	 private Rectangle rectangleCanoEmBaixo;

	//Atributos de configurações
	private float larguraDoDispositivo;
	private float alturaDoDispositivo;
	private float variacao = 0;
	private float gravity = 0;
	private float posicaoInicialPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int pontuacaoMAx = 0;
	private boolean passouCano;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;

	//Exibição de textos
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	//Configurar sons
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	//Objeto salvar pontuacao
	Preferences preferences;

	//Objetos para a camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;

	@Override
	public void create () {

		inicializarObjetos();
		inicializarTexturas();
	}

	@Override
	public void render () {

		//Limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoDoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();


	}



	private void verificarEstadoDoJogo(){

		boolean toqueTela = Gdx.input.justTouched();

		if( estadoJogo == 0 ){

			/* Aplica evento de toque na tela */
			if( toqueTela ){
				gravity = -15;
				estadoJogo = 1;
				somVoando.play();
			}

		}else if( estadoJogo == 1 ){

			/* Aplica evento de toque na tela */
			if( toqueTela ){
				gravity = -15;
				somVoando.play();
			}

			/*Movimentar o cano*/
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if( posicaoCanoHorizontal < -canoEmCima.getWidth() ){
				posicaoCanoHorizontal = larguraDoDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}

			/* Aplica gravidade no pássaro */
			if( posicaoInicialPassaro > 0 || toqueTela )
				posicaoInicialPassaro = posicaoInicialPassaro - gravity;

			gravity++;

		}else if( estadoJogo == 2 ){

//			/* Aplica gravidade no pássaro */
//			if( posicaoInicialPassaro > 0 || toqueTela )
//				posicaoInicialPassaro = posicaoInicialPassaro - gravity;
//			gravity++;
			if (pontos > pontuacaoMAx){
				pontuacaoMAx = pontos;
				preferences.putInteger("pontuacaoMax", pontuacaoMAx);
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;

			if( toqueTela ){
				estadoJogo = 0;
				pontos = 0;
				gravity = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialPassaro = alturaDoDispositivo / 2;
				posicaoCanoHorizontal = larguraDoDispositivo;
			}

		}

	}



	private void detectarColisoes(){

		circlePassaro.set(
				50 + posicaoHorizontalPassaro +passaros[0].getWidth() / 2 ,posicaoInicialPassaro + passaros[0].getHeight()/2,passaros[0].getWidth()/2
		);

		rectangleCanoEmBaixo.set(
				posicaoCanoHorizontal, alturaDoDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);
		rectangleCanoCima.set(
				posicaoCanoHorizontal,alturaDoDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoEmCima.getWidth(), canoEmCima.getHeight()
		);

		boolean colidiuCanoCima = Intersector.overlaps(circlePassaro, rectangleCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circlePassaro, rectangleCanoEmBaixo);

		if( colidiuCanoCima || colidiuCanoBaixo ){
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo = 2;
			}

		}

	}

	private void desenharTexturas(){

		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(fundo,0,0,larguraDoDispositivo, alturaDoDispositivo);
		batch.draw( passaros[ (int) variacao] , 50 + posicaoHorizontalPassaro, posicaoInicialPassaro);
		batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDoDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical);
		batch.draw(canoEmCima, posicaoCanoHorizontal,alturaDoDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical );
		textoPontuacao.draw(batch, String.valueOf(pontos),larguraDoDispositivo / 2,  alturaDoDispositivo -110 );

		if( estadoJogo == 2 ){
			batch.draw(gameOver, larguraDoDispositivo / 2 - gameOver.getWidth()/2, alturaDoDispositivo / 2 );
			textoReiniciar.draw(batch, "Toque para reiniciar!", larguraDoDispositivo/2 -140, alturaDoDispositivo/2 - gameOver.getHeight()/2 );
			textoMelhorPontuacao.draw(batch, "Seu record é: " + pontuacaoMAx + " pontos", larguraDoDispositivo/2 -140,alturaDoDispositivo/2 - gameOver.getHeight());
		}

		batch.end();

	}

	public void validarPontos(){

		if( posicaoCanoHorizontal < 50-passaros[0].getWidth() ){//Passou da posicao do passaro
			if(!passouCano){
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}

		variacao += Gdx.graphics.getDeltaTime() * 10;
		/* Verifica variação para bater asas do pássaro*/
		if (variacao > 3 )
			variacao = 0;

	}

	private void inicializarTexturas(){
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoEmCima = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");

	}

	private void inicializarObjetos(){

		batch = new SpriteBatch();
		random = new Random();

		larguraDoDispositivo = VIRTUAL_WIDTH;
		alturaDoDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialPassaro = alturaDoDispositivo / 2;
		posicaoCanoHorizontal = larguraDoDispositivo;
		espacoEntreCanos = 350;

		//Configurações dos textos
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		//Formas Geeométricas para colisoes;
		shapeRenderer = new ShapeRenderer();
		circlePassaro = new Circle();
		rectangleCanoEmBaixo = new Rectangle();
		rectangleCanoCima = new Rectangle();

		//Inicializa sons
		somVoando = Gdx.audio.newSound( Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound( Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound( Gdx.files.internal("som_pontos.wav"));

		//Configurar preferencias
		preferences = Gdx.app.getPreferences("flappyBird");
		pontuacaoMAx = preferences.getInteger("pontuacaoMax", 0);

		//cONFIGURA CAMERA
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport( VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);


	}

	@Override
	public void resize(int width, int height){
		viewport.update(width, height);
	}

	@Override
	public void dispose () {

	}
}
