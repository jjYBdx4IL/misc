var appFineUploaderRoot = assetsUri + 'fineuploader/';

requirejs.config({
	"baseUrl": assetsUri,
    "paths": {
      "jquery" : "//ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min",
      "blockUI" : "//cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.min",
      "blockadblock" : "//cdnjs.cloudflare.com/ajax/libs/blockadblock/3.2.1/blockadblock.min",
      "jquery-ui" : "//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min",
      "waypoints" : "//cdnjs.cloudflare.com/ajax/libs/waypoints/4.0.1/noframework.waypoints.min",
      "showdown" : "//cdn.rawgit.com/showdownjs/showdown/1.7.4/dist/showdown.min",
      "purify" : "//cdnjs.cloudflare.com/ajax/libs/dompurify/1.0.2/purify.min",
      "cookieconsent" : "//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.4/cookieconsent.min",
      "fineuploader" : appFineUploaderRoot + "jquery.fine-uploader.min",
      "imagesLoaded" : "//cdnjs.cloudflare.com/ajax/libs/jquery.imagesloaded/4.1.3/imagesloaded.pkgd.min",
    },
    "shim": {
        "blockUI": ["jquery"],
        "blockadblock": ["jquery"],
        "jquery-ui": ["jquery"],
        "fineuploader": ["jquery"],
        "imagesLoaded": ["jquery"],
    }
});

if ((typeof isDevel !== typeof undefined) && isDevel) {
    requirejs(["main"]);
} else {
    requirejs(["site.min"]);
}
