package controllers;

import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HelloController extends Controller {

    @Inject
    public HelloController() {
    }

    public Result hello(String name) {
        return ok("Hello, " + name);
    }

    public Result index() {
        return ok("Hello, world");
    }
}