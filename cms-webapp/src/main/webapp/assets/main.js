/* adblocker-blocker */

if ((typeof enableAdblockBlocker !== typeof undefined) && enableAdblockBlocker) {
    function adBlockDetected() {
        /* http://malsup.com/jquery/block/#options */
        $.blockUI({
            message : '<h1>Ad-blocker detected. Deactivate, then reload the page.</h1>',
            overlayCSS : {
                backgroundColor : '#000',
                opacity : 1.0
            }
        });
    }
    requirejs([ "jquery", "blockUI", "blockadblock" ], function($) {
        if (typeof blockAdBlock === 'undefined') {
            adBlockDetected();
        } else {
            blockAdBlock.onDetected(adBlockDetected);
        }

        blockAdBlock.check();
    }, function(err) {
        adBlockDetected();
    });
}

/* header */

requirejs([ "jquery" ], function($) {
    var didScroll;
    var lastScrollTop = 0;
    var delta = 5;
    var navbarHeight = $('header').outerHeight();
    var headerState = 1;

    $(window).scroll(function(event) {
        didScroll = true;
    });

    setInterval(function() {
        if (didScroll) {
            hasScrolled();
            didScroll = false;
        }
    }, 250);

    function hasScrolled() {
        var st = $(this).scrollTop();

        if (headerState == 0 && st < delta) {
            $('header').removeClass('nav-up').addClass('nav-down');
            headerState = 1;
            lastScrollTop = st;
            return;
        }

        // Make sure they scroll more than delta
        if (Math.abs(lastScrollTop - st) <= delta)
            return;

        // If they scrolled down and are past the navbar, add class .nav-up.
        // This is necessary so you never see what is "behind" the navbar.
        if (headerState == 1 && st > lastScrollTop && st > navbarHeight) {
            // Scroll Down
            $('header').removeClass('nav-down').addClass('nav-up');
            headerState = 0;
            lastScrollTop = st;
        }
    }
});

/*
 * trivial drop-down menu: it's always below the header bar, but only when the user click on the menu icon the header
 * bar gets extended downwards and reveals the menu's contents.
 */
requirejs([ "jquery" ], function($) {
    var rememberedHeight = $("header").css("height");
    var state = 0;
    $(".menuIcon").click(function() {
        var height = $("header").css("height");
        if (state == 1) {
            $("header").css("height", rememberedHeight);
            state = 0;
        } else {
            $("header").css("height", "auto");
            state = 1;
        }
    });
});

if ((typeof enableJsEditorSupport !== typeof undefined) && enableJsEditorSupport) {
    requirejs([ "jquery", "showdown", "purify", "jquery-ui" ], function($, showdown, DOMPurify) {

        /* https://jqueryui.com/autocomplete/#multiple-remote */
        /* tag-completion in editor */

        function split(val) {
            return val.split(/,\s*/);
        }
        function extractLast(term) {
            return split(term).pop();
        }

        $("#tags")
        // don't navigate away from the field on tab when selecting an item
        .on("keydown", function(event) {
            if (event.keyCode === $.ui.keyCode.TAB && $(this).autocomplete("instance").menu.active) {
                event.preventDefault();
            }
        }).autocomplete({
            source : function(request, response) {
                $.getJSON(tagSearchApiEndpoint, {
                    term : extractLast(request.term)
                }, response);
            },
            search : function() {
                // custom minLength
                var term = extractLast(this.value);
                if (term.length < 1) {
                    return false;
                }
            },
            focus : function() {
                // prevent value inserted on focus
                return false;
            },
            select : function(event, ui) {
                var terms = split(this.value);
                // remove the current input
                terms.pop();
                // add the selected item
                terms.push(ui.item.value);
                // add placeholder to get the comma-and-space at the end
                terms.push("");
                this.value = terms.join(", ");
                return false;
            }
        });

        /* article pathId generation */

        var dict = new Map([ [ '\u00dc', 'Ue' ], [ '\u00fc', 'ue' ], [ '\u00c4', 'Ae' ], [ '\u00e4', 'ae' ],
                [ '\u00d6', 'Oe' ], [ '\u00f6', 'oe' ], [ '\u00df', 'ss' ] ]);

        function t(match, val) {
            dict.forEach(function(value, key, map) {
                val = val.replace(new RegExp(key, "g"), value);
            });
            return val;
        }
        function toPathId(a) {
            a = a.replace(/([^a-z0-9]+)/gi, t);
            return a.replace(/([^a-z0-9]+)/gi, '-').toLowerCase();
        }

        $(".editForm input[name=title]").on("change paste keyup", function() {
            $(".editForm input[name=pathId]:not([readonly])").val(toPathId($(this).val()));
        });

        /* markdown preview, sanitize */

        var converter = new showdown.Converter();
        converter.setFlavor('ghost');
        converter.setOption('excludeTrailingPunctuationFromURLs', 'true');
        var textarea = $(".editForm textarea[name=content]");
        var preview = $("#mdPreview");
        function updateMdPreview() {
            $(preview).html(DOMPurify.sanitize(converter.makeHtml(textarea.val())));
            /* increase text area height to match preview area height */
            if ($(preview).height() > textarea.height()) {
                textarea.height($(preview).height());
            }
        }
        textarea.on("change paste keyup", updateMdPreview);

        // add processed markdown to form submission
        $(".editForm").submit(function(e) {
            $(this).find("[name=processed]").val(converter.makeHtml($(this).find("[name=content]").val()));
        });

        // intial preview
        updateMdPreview();
    });
}

// replace simple youtube links with iframes
requirejs([ "jquery", "waypoints" ], function($) {
    var myRegex = /(\d+)([hms])/gi;

    function toSecs(val) {
        var secs = 0;
        while (res = myRegex.exec(val)) {
            if (res[2] === 's') {
                secs += 1 * res[1];
            } else if (res[2] === 'm') {
                secs += 60 * res[1];
            } else if (res[2] === 'h') {
                secs += 3600 * res[1];
            }
        }
        return secs;
    }
    function toYoutubeIframe(val, vidId, startPos) {
        var url = 'https://www.youtube.com/embed/' + vidId;
        if (startPos) {
            url += '?start=' + toSecs(startPos);
        }
        return '<iframe width="560" height="315" src="' + url + '" frameborder="0" allowfullscreen></iframe>';
    }
    function embedStuff(target) {
        if ($(target).text().match(/\bembed:\/\/youtube\/([a-z0-9_-]{8,})(\/([0-9hms]+))?\b/gi)) {
            $(target).html(
                $(target).html().replace(/\bembed:\/\/youtube\/([a-z0-9_-]{8,})(\/([0-9hms]+))?\b/gi, toYoutubeIframe));
        }
    }
    function embedStuffDelayed(target) {
        if ($(target).text().match(/\bembed:\/\//i)) {
            // http://imakewebthings.com/waypoints/guides/getting-started/
            var wp = new Waypoint({
                element : target,
                handler : function() {
                    embedStuff(target);
                    wp.destroy();
                },
                offset : '100%'
            });
        }
    }
    $(".articleContent").each(function(index) {
        embedStuffDelayed(this);
    });

});

if ((typeof cookieConsentMessage !== typeof undefined) && cookieConsentMessage) {
    requirejs([ "jquery", "cookieconsent" ], function($) {
        $(window).ready(function() {
            window.cookieconsent.initialise({
                "palette" : {
                    "popup" : {
                        "background" : "#000000",
                        "text" : "#cccccc"
                    },
                    "button" : {
                        "background" : "#cfcfcf"
                    }
                },
                "theme" : "edgeless",
                "content" : {
                    "message" : cookieConsentMessage,
                    "href" : privacyPolicyUri
                }
            })
        });
    });
}

if ((typeof fineUploaderEndpoint !== typeof undefined) && fineUploaderEndpoint) {
    requirejs([ "jquery", "fineuploader" ], function($) {
        $('#fine-uploader-gallery').fineUploader({
            template : 'qq-template-gallery',
            request : {
                endpoint : fineUploaderEndpoint
            },
            thumbnails : {
                placeholders : {
                    waitingPath : appFineUploaderRoot + 'placeholders/waiting-generic.png',
                    notAvailablePath : appFineUploaderRoot + 'placeholders/not_available-generic.png'
                }
            },
            validation : {
                allowedExtensions : [ 'jpeg', 'jpg', 'gif', 'png' ]
            }
        });
    });
}
