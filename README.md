# Simple AJAX demo based on Java

A demo web application with AJAX support, based on the [Ujorm](https://ujorm.org/) framework and Vanilla JavaScript [ES6](https://en.wikipedia.org/wiki/ECMAScript#6th_Edition_%E2%80%93_ECMAScript_2015).

## Quick overview

The central entry point of this demo is `TutorialServlet`, which handles:

- full-page rendering in `doGet()`
- AJAX partial updates in `doPost()`

For implementation details, see the main [servlet code](https://github.com/pponec/demo-ajax/blob/main/src/main/java/net/ponec/demo/servlet/TutorialServlet.java).

## Screenshot

<p align="center">
  <img src="docs/screen.jpg" alt="Tutorial screen" width="350">
</p>

## Element tutorial

This tutorial explains how to build HTML pages and AJAX responses in this project using:

- `TutorialServlet` - orchestration of the GET/POST flow
- `Element` - fluent builder for HTML tags and attributes
- `HtmlElement` - document root (`html`, `head`, `body`) and output configuration
- `JsonBuilder` - JSON response builder for partial page updates

The guide is written to be usable by both humans and AI clients.

### 1) Mental model

Use this model:

1. `doGet()` renders the full page.
2. `doPost()` + AJAX parameter returns a JSON map `"selector -> new HTML content"`.
3. `Element` composes tags via chain methods (`addDiv().addHeading().setClass(...)`).
4. `JsonBuilder` returns HTML fragments escaped into JSON string values.

In `TutorialServlet`, this means:

- GET builds a form with an input and output box (`Css.output`).
- POST returns new content for `.ajax-output` on AJAX requests.
- without AJAX, POST falls back to classic server-side rendering (`doGet()`).

### 2) `HtmlElement`: document entry point

`HtmlElement` / `AbstractHtmlElement` is the entry point for the full HTML document:

- opens and closes the root document (`try-with-resources`)
- keeps singleton `head` and `body`
- supports configuration (`title`, CSS links, `charset`, "nice format")

Typical pattern:

```java
try (var html = AbstractHtmlElement.of("Page title", ctx)) {
    html.getHead().addStyle().addRawText("/* css */");
    try (var body = html.addBody()) {
        body.addHeading("Hello");
    }
}
```

### 3) `Element`: HTML building blocks

`Element` is a fluent API on top of an HTML/XML builder. Most important rules:

- `addXxx()` creates a child element (`addDiv`, `addForm`, `addInput`, ...)
- `setXxx()` sets an attribute (`setClass`, `setName`, `setValue`, ...)
- `addText()` escapes text (safe for normal content)
- `addRawText()` writes raw text (use only for trusted content, e.g. internal CSS/JS)

Examples:

```java
body.addDiv("card")
    .addHeading("Title")
    .addParagraph().addText("Safe text");
```

```java
form.addInput("my-input")
    .setType(Html.V_TEXT)
    .setName("query")
    .setValue("abc");
```

#### Important performance note

`Element.addElement(...)` may internally return a reused child builder instance.
So avoid storing sibling child references for longer than needed when creating more elements on the same level. Safe patterns are:

- compose chains inline
- or use short `try (...) { ... }` blocks as in `TutorialServlet`

### 4) `TutorialServlet`: GET and POST flow step by step

#### GET (`doGet`)

1. create `ExchangeContext`
2. open HTML document
3. add CSS and JavaScript to `head` (`JavaScriptWriter`)
4. build a form in `body`:
   - input (`TEXT`)
   - submit button
   - output box with CSS class `ajax-output`
5. fill output content via `printResult(...)`

#### POST (`doPost`)

1. read `DEFAULT_AJAX_REQUEST_PARAM`
2. if `true`, return JSON via `JsonBuilder`
3. JSON includes key `.ajax-output` and value = new HTML fragment
4. if missing, call `doGet()` (non-AJAX fallback)

### 5) `JsonBuilder`: server-side diff for frontend

`JsonBuilder` creates a simple JSON object. The key is a CSS selector:

- `writeId("result", ...)` -> key `"#result"`
- `writeClass("ajax-output", ...)` -> key `".ajax-output"`
- `write("key", ...)` -> key `"key"` (no prefix)

In this project:

```java
try (var json = JsonBuilder.of(ctx)) {
    json.writeClass(Css.output, e -> printResult(e, ctx));
}
```

This means: "replace the content of all elements with class `.ajax-output` with newly generated HTML from `Element`."

### 6) Practical template for a new page

Use this procedure:

1. Create servlet `@WebServlet("/my-page")`.
2. In `doGet()`, build the full page with `AbstractHtmlElement.of(title, ctx)`.
3. Give interactive blocks stable CSS classes or IDs.
4. In `doPost()`, split AJAX vs non-AJAX logic.
5. For AJAX responses, return fragments only via `JsonBuilder`.
6. Generate fragments using the same method as server-side rendering (DRY), e.g. `printResult(...)`.

### 7) Conventions for AI clients (prompt-ready)

If an AI client receives this README, follow these rules:

- Use `try-with-resources` for `HtmlElement`, `Element`, and `JsonBuilder`.
- Build HTML with the `Element` fluent API, not by manual string concatenation.
- Use `addText()` for normal content; use `addRawText()` only for trusted raw content.
- For AJAX updates, return JSON map selector -> HTML (`writeId` / `writeClass`).
- Reuse existing selector constants (`Css.output`, etc.) instead of ad-hoc strings.
- Keep rendering logic in shared methods (`printResult(...)`) so GET and POST produce identical output.

### 8) Most common mistakes

- missing `DEFAULT_AJAX_REQUEST_PARAM` -> client expects JSON, server returns full HTML page
- using `addRawText()` for user input -> XSS risk
- selector mismatch between frontend and `JsonBuilder` -> update is not applied
- duplicated render logic in GET/POST -> inconsistent UI

## Maven Dependency

Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>org.ujorm</groupId>
    <artifactId>ujo-web</artifactId>
    <version>latest</version>
</dependency>
```

For production use, prefer pinning a concrete version instead of `latest`.

## JavaDoc

- `Element`: [JavaDoc](https://www.javadoc.io/doc/org.ujorm/ujo-web/latest/org/ujorm/tools/web/Element.html)
- `HtmlElement`: [JavaDoc](https://www.javadoc.io/doc/org.ujorm/ujo-web/latest/org/ujorm/tools/web/HtmlElement.html)
- `JsonBuilder`: [JavaDoc](https://www.javadoc.io/doc/org.ujorm/ujo-web/latest/org/ujorm/tools/web/json/JsonBuilder.html)

## System Requirements:

* Java Development Kit version 8 (LTS)
* A web browser supported Javascript [ES6](https://en.wikipedia.org/wiki/ECMAScript#6th_Edition_%E2%80%93_ECMAScript_2015)
* An internet connection

## How to run the project:

On Windows:

```sh
cd project-dir
.\run.cmd

start firefox localhost:8080
```

On Linux:

```sh
cd project-dir
./run.sh

firefox localhost:8080
```

## Internet Links

* Ujorm home page: https://ujorm.org/
* Javascript ES6 [guide](https://www.freecodecamp.org/news/a-practical-es6-guide-on-how-to-perform-http-requests-using-the-fetch-api-594c3d91a547/) for using the Fetch API
* License: [Apache License, Version 2.0, January 2004](LICENSE.txt)
* Project Home Page: https://github.com/pponec/demo-ajax
