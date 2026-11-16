package com.tallerwebi.dominio.Producto;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

// Foto neutra para los productos que no tienen una foto exacta (sin marca, a granel, carnicería,
// verdulería). No se busca ni se adivina nada: cada producto se reconoce por palabras de su nombre y
// se le asigna una foto ya revisada de Spoonacular, que publica imágenes de ingredientes sobre fondo
// blanco. Lo que no figura en la tabla queda sin foto y la vista muestra el ícono gris.
@Service("servicioFotoGenerica")
public class ServicioFotoGenericaImpl implements ServicioFotoGenerica {

  private static final String BASE = "https://spoonacular.com/cdn/ingredients_250x250/";

  // Lo que vuelve a un producto "elaborado": un jugo, un dulce o un limpiador de limón no es un limón
  private static final String ELABORADO =
    "jugo|mermelada|dulce|pure|compota|conserva|lata|congelad|deshidrat|seco|almibar|gelatina|" +
    "saborizad|aroma|sabor|galleta|alfajor|helado|budin|tarta|jabon|deterg|limpi|lavandina|" +
    "lavavaj|desodor|crema|shampoo|enjuague|gaseosa|aderezo|salsa|aceite|vinagre|esencia|polvo|" +
    "yogur|licor|sidra|cerveza|vino|snack|chips|frita";

  // Una línea por foto: archivo ; palabras del nombre ; palabras que lo descartan (opcional). Un
  // producto toma la primera línea que lo incluye y no lo excluye, por eso las más específicas
  // (pechuga, tomate cherry, aceite de oliva) van antes que las generales (pollo, tomate, aceite).
  private static final String TABLA =
    """
    milk.jpg ; leche ; condensada|chocolatada|polvo|coco|dulce|soja|almendra|avena
    butter.jpg ; manteca ; mani
    coffee.jpg ; cafe
    cream-cheese.jpg ; queso crema|queso untable|crema de queso
    mozzarella.png ; mozzarella|muzzarella|mozarella|muzarela|mozzarela|muzzarela|mozarela|muzarella
    ricotta.png ; ricota|ricotta
    gouda.jpg ; gouda
    parmesan.jpg ; parmesano|reggianito|sardo
    cheddar-cheese.jpg ; cheddar
    flour.jpg ; harina ; maiz|mani|almendra|arroz|garbanzo|mandioca|coco|centeno
    hot-dog-buns.jpg ; pan de pancho|panchos
    white-bread.jpg ; pan de molde|pan lactal
    salt.jpg ; sal
    macaroni.jpg ; codito|macarron|macarrones
    penne.jpg ; penne|mostachol|rigatoni
    spaghetti.jpg ; espagueti|spaghetti|tallarin|tallarines|fideos ; tirabuz|mono|dedal|municion|ravio|noqui|nido|caracol|fusil|sopa
    uncooked-white-rice.png ; arroz ; integral|leche|harina|galleta|torta|crocante|bebida|vegetal
    sparkling-water.png ; soda|agua con gas|agua gasificada
    water.jpg ; agua ; gas|saborizad|tonica|oxigenada|colonia|florida|micelar|perfumada|destilada|rosas|coco
    sunflower-seeds.jpg ; semillas de girasol|pepitas de girasol
    olive-oil.jpg ; aceite de oliva
    vegetable-oil.jpg ; aceite ; oliva|coco|sesamo|motor
    barbecue-sauce.jpg ; salsa barbacoa|barbacoa|bbq
    ketchup.jpg ; ketchup|catsup
    mustard.jpg ; mostaza
    honey.jpg ; miel ; jabon|shampoo|crema
    yeast.jpg ; levadura
    cocoa-powder.jpg ; cacao ; leche|chocolatada|galleta
    peanut-butter.jpg ; pasta de mani|manteca de mani
    peanuts.jpg ; mani ; manteca|pasta|aceite|salsa
    walnuts.jpg ; nuez|nueces
    almonds.jpg ; almendra|almendras ; leche
    raisins.jpg ; pasas de uva|pasas
    wine.jpg ; vino
    beer.jpg ; cerveza
    orange-juice.jpg ; jugo de naranja
    egg.jpg ; huevo|huevos
    ham.jpg ; jamon
    mortadella.jpg ; mortadela
    salami.jpg ; salame|salamin
    chorizo.jpg ; chorizo
    chicken-breast.jpg ; pechuga
    chicken-thighs.png ; muslo
    whole-chicken.jpg ; pollo ; pechuga|muslo|milanesa|nugget|hamburguesa|suprema|empanado|caldo
    pork-loin.jpg ; bondiola|lomo de cerdo|carre de cerdo
    pork-chops.jpg ; chuleta|costeleta
    ribs.jpg ; costilla|costillar|asado|tira de asado
    flank-steak.jpg ; vacio|entrana|matambre
    roast-beef.jpg ; peceto|nalga|cuadril|bola de lomo|roast beef
    beef-tenderloin.jpg ; lomo ; cerdo|atun|jamon|ahumado|embuchado
    salmon.jpg ; salmon
    shrimp.jpg ; langostino|camaron|gamba
    fish-fillet.jpg ; merluza|pescado|abadejo|brotola|lenguado
    cherry-tomatoes.jpg ; tomate cherry|tomates cherry|tomatitos
    tomato.jpg ; tomate|tomates ; pure|salsa|triturado|extracto|conserva|lata|pelado|seco|ketchup|jugo
    tomato-paste.jpg ; extracto de tomate|pasta de tomate
    sweet-potato.jpg ; batata|boniato ; frita|pure|congelad
    red-potatoes.jpg ; papa|papas ; frita|pure|congelad|snack|chips|noisette|bastones
    green-onion.jpg ; cebolla de verdeo|verdeo|cebolla verde
    red-onion.jpg ; cebolla morada|cebolla colorada
    white-onion.jpg ; cebolla ; polvo|deshidrat|frita|aros
    garlic.jpg ; ajo ; polvo|salsa|pasta
    carrots.jpg ; zanahoria ; jugo|pure|congelad|rallada
    iceberg-lettuce.jpg ; lechuga
    spinach.jpg ; espinaca ; congelad|crema
    swiss-chard.jpg ; acelga ; congelad
    red-cabbage.jpg ; repollo colorado|repollo morado
    cabbage.jpg ; repollo
    brussels-sprouts.jpg ; repollitos de bruselas|coles de bruselas
    broccoli.jpg ; brocoli ; congelad
    cauliflower.jpg ; coliflor ; congelad
    celery.jpg ; apio
    cucumber.jpg ; pepino ; encurtido|pickle
    zucchini.jpg ; zapallito|zucchini|calabacin
    butternut-squash.jpg ; zapallo anco|anco|butternut
    pumpkin.jpg ; zapallo|calabaza ; pure|conserva|lata
    eggplant.jpg ; berenjena ; conserva|escabeche
    green-bell-pepper.jpg ; morron verde|pimiento verde
    red-bell-pepper.jpg ; morron|morrones|pimiento ; polvo|conserva|lata|asado
    corn.png ; choclo|maiz ; harina|polenta|aceite|almidon|maizena|pochoclo|pop|cereal|copos|lata
    mushrooms.jpg ; champinon|champinones|hongos ; conserva|lata
    avocado.jpg ; palta|aguacate
    beets.jpg ; remolacha ; conserva|lata
    fennel.jpg ; hinojo
    ginger.jpg ; jengibre ; polvo|cerveza|gaseosa
    parsley.jpg ; perejil ; polvo|deshidrat|seco
    basil.jpg ; albahaca
    cilantro.png ; cilantro
    mint.jpg ; menta ; caramelo|chicle|pasta|dental|enjuague|jabon|shampoo|gaseosa
    oregano.jpg ; oregano
    rosemary.jpg ; romero
    thyme.jpg ; tomillo
    peas.jpg ; arveja|arvejas ; conserva|lata|congelad
    chickpeas.jpg ; garbanzo|garbanzos ; conserva|lata
    black-beans.jpg ; porotos negros
    apple.jpg ; manzana ; ELABORADO
    bananas.jpg ; banana|bananas|platano ; ELABORADO
    orange.jpg ; naranja ; ELABORADO
    lemon.jpg ; limon ; ELABORADO
    lime.jpg ; lima ; una|unas|ELABORADO
    strawberries.jpg ; frutilla|frutillas|fresa|fresas ; ELABORADO
    watermelon.jpg ; sandia ; ELABORADO
    pineapple.jpg ; anana|pina ; ELABORADO
    pear.jpg ; pera ; ELABORADO
    peaches.jpg ; durazno|duraznos ; ELABORADO
    kiwi.png ; kiwi ; ELABORADO
    mango.jpg ; mango ; ELABORADO
    cantaloupe.jpg ; melon ; ELABORADO
    cherries.jpg ; cereza|cerezas ; ELABORADO
    blueberries.jpg ; arandano|arandanos ; ELABORADO
    raspberries.jpg ; frambuesa|frambuesas ; ELABORADO
    pomegranate.jpg ; granada ; ELABORADO
    papaya.jpg ; papaya ; ELABORADO
    grapefruit.jpg ; pomelo ; ELABORADO
    coconut.jpg ; coco ; leche|ELABORADO
    dates.jpg ; datil|datiles ; ELABORADO
    """.replace("ELABORADO", ELABORADO);

  private static final List<Foto> FOTOS = TABLA.lines().map(Foto::deLinea).toList();

  @Override
  public String buscarFoto(String nombre, String categoria) {
    if (nombre == null || nombre.isBlank()) {
      return null;
    }
    String texto = normalizar(nombre);
    for (Foto foto : FOTOS) {
      if (foto.corresponde(texto)) {
        return BASE + foto.archivo();
      }
    }
    return null;
  }

  private static String normalizar(String texto) {
    return Normalizer
      .normalize(texto, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);
  }

  private record Foto(String archivo, Pattern incluye, Pattern excluye) {
    private static final Pattern NUNCA = Pattern.compile("(?!)");

    // Las palabras que incluyen tienen que ser palabras enteras (con plural); las que excluyen
    // alcanzan con que el nombre empiece así, así "pure" también descarta "puré"
    static Foto deLinea(String linea) {
      String[] partes = linea.split(";");
      String excluidas = partes.length > 2 ? partes[2].trim() : "";
      return new Foto(
        partes[0].trim(),
        Pattern.compile("\\b(" + partes[1].trim() + ")s?\\b"),
        excluidas.isEmpty() ? NUNCA : Pattern.compile("\\b(" + excluidas + ")")
      );
    }

    boolean corresponde(String texto) {
      return incluye.matcher(texto).find() && !excluye.matcher(texto).find();
    }
  }
}
