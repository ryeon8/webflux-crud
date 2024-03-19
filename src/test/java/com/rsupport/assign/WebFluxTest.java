package com.rsupport.assign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

public class WebFluxTest {

  @Test
  public void test3() {
    Map<String, String> a = new HashMap<>();
    a.put("aa", "bb");
    Mono<Map<String, String>> mono = Mono.just(a);
    System.out.println(mono.block().get("aa"));

  }

  @Test
  public void test1() {
    Flux<String> fileIdListFlux = Flux.just("1", "2");
    Mono<String> notiIdMono = Mono.fromSupplier(() -> {
      System.out.println("notiIdMono published"); // 구독할 때마다 수행됨.
      return "a";
    }).cache();

    notiIdMono.subscribe(System.out::println); // 둘 다 구독 로그 출력됨. cache하면 한번만!
    notiIdMono.subscribe(System.out::println);

    // fileIdListFlux.zipWith(notiIdMono.flux()).subscribe(System.out::println); //
    // 1, a
    notiIdMono.flatMapMany(m -> fileIdListFlux.map(x -> Tuples.of(x, m))).subscribe(e -> {
      System.out.println(e);
    });

  }

  @Test
  public void test0() {
    Mono<String> mono = Mono.just("a");
    String a = mono.block();
    System.out.println(a);
    System.out.println("b");
  }

  @Test
  public void test() {
    Flux<String> stringFlux = Flux.just("a", "b", "c")
        .flatMap(e -> {
          try {
            System.out.println("wait 1s");
            Thread.sleep(1000);
          } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          return Flux.just(e);
        });

    System.out.println("before subscribing");

    stringFlux.subscribe(System.out::println);
  }

  @Test
  public void test4() {
    List<Boolean> list = IntStream.range(0, 3).mapToObj(i -> {
      // return ((i + 1) % 2) == 0;
      return false;
    }).toList();

    boolean r = list.stream().allMatch(Boolean::booleanValue);
    System.out.println(r);
  }
}
