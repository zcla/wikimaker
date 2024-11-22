// ==UserScript==
// @name         Bíblia de Jerusalém
// @namespace    http://tampermonkey.net/
// @version      2024-11-21
// @description  try to take over the world!
// @author       You
// @match        https://liturgiadashoras.online/biblia/biblia-jerusalem/*
// @icon         https://www.google.com/s2/favicons?sz=64&domain=liturgiadashoras.online
// @grant        GM_setClipboard
// @require      http://code.jquery.com/jquery-3.7.1.min.js
// ==/UserScript==

// NOTA
// Quando o script foi rodado pela última vez, o livro "iudae" (Epístola de Judas) não apresentava lista de capítulos. Acrescentando um "/1-2" à URL, como acontece com os outros capítulos, é possível acessar o capítulo 1, e o script acrescenta automaticamente no json final.

(function() {
    'use strict';
    const urlBase = 'https://liturgiadashoras.online/biblia/biblia-jerusalem/';
    const urlLivroRegex = /^https:\/\/liturgiadashoras\.online\/biblia\/biblia-jerusalem\/([^\/]+)\/$/;
    const urlCapituloRegex = /^https:\/\/liturgiadashoras\.online\/biblia\/biblia-jerusalem\/([^\/]+)\/([\d\-]+)\/$/;
debugger;
    let obj = null;
    let json = localStorage.getItem('bibladejerusalem');
    if (json) {
        obj = JSON.parse(json);
    }

    if (obj === null) {
        if (document.URL != urlBase) {
            window.location.href = urlBase;
            return;
        }
        obj = {
            urls: [],
            data: {}
        };

        // Lista de livros
        const aLivros = $('a').filter(function(index, element) { return element.href.match(urlLivroRegex) });
        for (const aLivro of aLivros) {
            obj.urls.push(aLivro.href);
        }
    }

    // Livro
    if (document.URL.match(urlLivroRegex)) {
        const aCapitulos = $('a').filter(function(index, element) { return element.href.match(urlCapituloRegex) });
        for (const aCapitulo of aCapitulos) {
            obj.urls.push(aCapitulo.href);
        }
    }

    // Capítulo
    if (document.URL.match(urlCapituloRegex)) {
        const key = document.URL.replace(urlCapituloRegex, '$1') + '/' + document.URL.replace(urlCapituloRegex, '$2');
        obj.data[key] = [];
        const partes = $('.entry-content').children();
        for (const parte of partes) {
            const data = parte.outerHTML;
            obj.data[key].push(data);
        }
    }

    // Próxima URL
    while (obj.urls.length > 0) {
        const proximaUrl = obj.urls.shift();
        const json = JSON.stringify(obj);
        localStorage.setItem('bibladejerusalem', json);
        window.location.href = proximaUrl;
        return;
    }

    // Fim
    GM_setClipboard(JSON.stringify(obj));
})();
