package com.tallerwebi.dominio.Producto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;

public class ServicioFotoGenericaTest {

  private final ServicioFotoGenericaImpl servicio = new ServicioFotoGenericaImpl();

  @Test
  public void laFotoDeLaLecheEsUnaFotoDeIngredienteDeSpoonacular() {
    String foto = servicio.buscarFoto("Leche", "Lacteos");

    assertThat(foto, startsWith("https://spoonacular.com/cdn/ingredients_250x250/"));
    assertThat(foto, endsWith("milk.jpg"));
  }

  @Test
  public void reconoceElProductoSinImportarMayusculasNiTildes() {
    assertThat(servicio.buscarFoto("JAMÓN cocido", "Fiambres"), endsWith("ham.jpg"));
    assertThat(servicio.buscarFoto("Pan de pancho x6", "Panaderia"), endsWith("hot-dog-buns.jpg"));
  }

  @Test
  public void laPechugaYElMusloTienenSuPropiaFotoYNoLaDelPolloEntero() {
    assertThat(
      servicio.buscarFoto("Pechuga de pollo", "Carnes y aves"),
      endsWith("chicken-breast.jpg")
    );
    assertThat(
      servicio.buscarFoto("Pata y muslo", "Carnes y aves"),
      endsWith("chicken-thighs.png")
    );
    assertThat(servicio.buscarFoto("Pollo entero", "Carnes y aves"), endsWith("whole-chicken.jpg"));
  }

  @Test
  public void reconocePiezasDeCarniceria() {
    assertThat(servicio.buscarFoto("Vacío", "Carnes y aves"), endsWith("flank-steak.jpg"));
    assertThat(servicio.buscarFoto("Tira de asado", "Carnes y aves"), endsWith("ribs.jpg"));
    assertThat(servicio.buscarFoto("Peceto", "Carnes y aves"), endsWith("roast-beef.jpg"));
    assertThat(servicio.buscarFoto("Lomo", "Carnes y aves"), endsWith("beef-tenderloin.jpg"));
    assertThat(servicio.buscarFoto("Bondiola", "Carnes y aves"), endsWith("pork-loin.jpg"));
    assertThat(servicio.buscarFoto("Merluza", "Pescados y mariscos"), endsWith("fish-fillet.jpg"));
  }

  @Test
  public void elLomoDeCerdoNoTomaLaFotoDelLomoVacuno() {
    assertThat(servicio.buscarFoto("Lomo de cerdo", "Carnes y aves"), endsWith("pork-loin.jpg"));
  }

  @Test
  public void laSalNoSeConfundeConLaSalsa() {
    assertThat(servicio.buscarFoto("Sal fina", "Almacen"), endsWith("salt.jpg"));
    assertThat(
      servicio.buscarFoto("Salsa barbacoa", "Condimentos y salsas"),
      endsWith("barbecue-sauce.jpg")
    );
    assertThat(servicio.buscarFoto("Salsa golf", "Condimentos y salsas"), is(nullValue()));
  }

  @Test
  public void elAceiteDeOlivaNoTomaLaFotoDelAceiteComun() {
    assertThat(servicio.buscarFoto("Aceite de girasol", "Almacen"), endsWith("vegetable-oil.jpg"));
    assertThat(servicio.buscarFoto("Aceite de oliva", "Almacen"), endsWith("olive-oil.jpg"));
    assertThat(servicio.buscarFoto("Aceite de coco", "Almacen"), is(nullValue()));
  }

  @Test
  public void losFideosConFormaEspecialNoTomanLaFotoDelEspagueti() {
    assertThat(servicio.buscarFoto("Fideos tallarines", "Almacen"), endsWith("spaghetti.jpg"));
    assertThat(servicio.buscarFoto("Fideos moños", "Almacen"), is(nullValue()));
  }

  @Test
  public void laLecheEnPolvoOElDulceDeLecheNoTomanLaFotoDeLaLeche() {
    assertThat(servicio.buscarFoto("Leche en polvo", "Lacteos"), is(nullValue()));
    assertThat(servicio.buscarFoto("Dulce de leche", "Almacen"), is(nullValue()));
  }

  @Test
  public void elPureDeTomateNoTomaLaFotoDelTomate() {
    assertThat(servicio.buscarFoto("Tomate perita", "Verduras y frutas"), endsWith("tomato.jpg"));
    assertThat(servicio.buscarFoto("Puré de tomate", "Almacen"), is(nullValue()));
  }

  @Test
  public void lasSemillasSinDetalleNoTienenFoto() {
    assertThat(servicio.buscarFoto("Semillas", "Almacen"), is(nullValue()));
    assertThat(
      servicio.buscarFoto("Semillas de girasol", "Almacen"),
      endsWith("sunflower-seeds.jpg")
    );
  }

  @Test
  public void unProductoQueNoEstaEnLaListaQuedaSinFoto() {
    assertThat(servicio.buscarFoto("Broches de plastico", "Ferreteria y bazar"), is(nullValue()));
    assertThat(servicio.buscarFoto("Jabon liquido", "Limpieza del hogar"), is(nullValue()));
  }

  @Test
  public void reconoceFrutasYVerdurasDeLaVerduleria() {
    assertThat(servicio.buscarFoto("Papa", "Verduras y frutas"), endsWith("red-potatoes.jpg"));
    assertThat(servicio.buscarFoto("Zanahoria", "Verduras y frutas"), endsWith("carrots.jpg"));
    assertThat(
      servicio.buscarFoto("Lechuga crespa", "Verduras y frutas"),
      endsWith("iceberg-lettuce.jpg")
    );
    assertThat(servicio.buscarFoto("Zapallito", "Verduras y frutas"), endsWith("zucchini.jpg"));
    assertThat(servicio.buscarFoto("Banana", "Verduras y frutas"), endsWith("bananas.jpg"));
    assertThat(servicio.buscarFoto("Manzana roja", "Verduras y frutas"), endsWith("apple.jpg"));
    assertThat(servicio.buscarFoto("Frutillas", "Verduras y frutas"), endsWith("strawberries.jpg"));
    assertThat(servicio.buscarFoto("Palta", "Verduras y frutas"), endsWith("avocado.jpg"));
  }

  @Test
  public void laCebollaDeVerdeoYLaMoradaTienenSuPropiaFoto() {
    assertThat(servicio.buscarFoto("Cebolla", "Verduras y frutas"), endsWith("white-onion.jpg"));
    assertThat(
      servicio.buscarFoto("Cebolla de verdeo", "Verduras y frutas"),
      endsWith("green-onion.jpg")
    );
    assertThat(
      servicio.buscarFoto("Cebolla morada", "Verduras y frutas"),
      endsWith("red-onion.jpg")
    );
  }

  @Test
  public void elTomateCherryYElZapalloAncoTienenSuPropiaFoto() {
    assertThat(
      servicio.buscarFoto("Tomate cherry", "Verduras y frutas"),
      endsWith("cherry-tomatoes.jpg")
    );
    assertThat(
      servicio.buscarFoto("Zapallo anco", "Verduras y frutas"),
      endsWith("butternut-squash.jpg")
    );
    assertThat(servicio.buscarFoto("Zapallo", "Verduras y frutas"), endsWith("pumpkin.jpg"));
  }

  @Test
  public void unProductoElaboradoNoTomaLaFotoDeLaFrutaQueNombra() {
    assertThat(servicio.buscarFoto("Jugo de limón", "Bebidas sin alcohol"), is(nullValue()));
    assertThat(servicio.buscarFoto("Detergente limón", "Limpieza del hogar"), is(nullValue()));
    assertThat(servicio.buscarFoto("Gaseosa naranja", "Bebidas sin alcohol"), is(nullValue()));
    assertThat(servicio.buscarFoto("Papas fritas", "Snacks y golosinas"), is(nullValue()));
    assertThat(servicio.buscarFoto("Lima de uñas", "Higiene personal"), is(nullValue()));
  }

  @Test
  public void laHarinaDeMaizNoTomaLaFotoDeLaHarinaDeTrigo() {
    assertThat(servicio.buscarFoto("Harina 000", "Almacen"), endsWith("flour.jpg"));
    assertThat(servicio.buscarFoto("Harina de maíz", "Almacen"), is(nullValue()));
  }

  @Test
  public void laLecheDeCocoNoTomaLaFotoDeLaLeche() {
    assertThat(servicio.buscarFoto("Leche de coco", "Almacen"), is(nullValue()));
  }

  @Test
  public void sinNombreNoHayFoto() {
    assertThat(servicio.buscarFoto(null, "Almacen"), is(nullValue()));
    assertThat(servicio.buscarFoto("  ", "Almacen"), is(nullValue()));
  }
}
