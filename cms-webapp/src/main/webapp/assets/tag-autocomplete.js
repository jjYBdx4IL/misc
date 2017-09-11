/* https://jqueryui.com/autocomplete/#multiple-remote */

$(function() {
	function split(val) {
		return val.split(/,\s*/);
	}
	function extractLast(term) {
		return split(term).pop();
	}

	$("#tags")
	// don't navigate away from the field on tab when selecting an item
	.on(
			"keydown",
			function(event) {
				if (event.keyCode === $.ui.keyCode.TAB
						&& $(this).autocomplete("instance").menu.active) {
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
});

/* article pathId generation */

$(document).ready(
		function() {
			var dict = new Map([ [ '\u00dc', 'Ue' ], [ '\u00fc', 'ue' ],
					[ '\u00c4', 'Ae' ], [ '\u00e4', 'ae' ], [ '\u00d6', 'Oe' ],
					[ '\u00f6', 'oe' ], [ '\u00df', 'ss' ] ]);

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

			$(".editForm input[name=title]").on(
					"change paste keyup",
					function() {
						$(".editForm input[name=pathId]").val(
								toPathId($(this).val()));
					});
		});

/* markdown preview */

$(document).ready(
		function() {
			var converter = new showdown.Converter();
			converter.setFlavor('ghost');
			function updateMdPreview() {
				$("#mdPreview").html(
						converter
								.makeHtml($(".editForm textarea[name=content]")
										.val()));
			}
			$(".editForm textarea[name=content]").on("change paste keyup",
					updateMdPreview);
			
			// intial preview
			updateMdPreview();
			
			// conversion of static markdown content
			$("div.articleContent").each(function( index ) {
				  $( this ).html(DOMPurify.sanitize(converter.makeHtml($( this ).text())));
			});
		});



