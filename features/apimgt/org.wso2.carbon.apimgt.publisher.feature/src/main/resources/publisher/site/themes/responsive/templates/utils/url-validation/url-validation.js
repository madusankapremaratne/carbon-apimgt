$(document).ready(function(){
    $( "body" ).delegate( "button.check_url_valid", "click", function() {
        var btn = this;
        var url = $(this).parent().parent().find('input:first').val();
        var type = '';
        var attr = $(this).attr('url-type');
        var thisID = $(this).attr('id');
        if (attr) {
            type = $(btn).attr('url-type');
        } else {
            if (thisID == "prototype_test") {
                type = "http";
            }
            else {
                type = $.parseJSON($("#endpoint_config").val())['endpoint_type']
            }
        }
        $(btn).parent().parent().find('.url_validate_label').remove();
        $(btn).addClass("loadingButton-small");
        $(btn).val(i18n.t('validationMsgs.validating'));

        if (url == '') {
            $(btn).parent().parent().after(' <span class="label label-danger url_validate_label"><i class="glyphicon glyphicon-exclamation-sign icon-white"></i>'+ i18n.t('validationMsgs.missingUrl')+'</span>');
            var toFade = $(btn).parent().parent().parent().find('.url_validate_label');
            $(btn).removeClass("loadingButton-small");
            $(btn).val(i18n.t('validationMsgs.testUri'));
            var foo = setTimeout(function(){$(toFade).hide()},3000);
            return;
        }
        if (!type) {
            type = "";
        }
        jagg.post("/site/blocks/item-add/ajax/add.jag", { action:"isURLValid", type:type,url:url },
                  function (result) {
                      if (!result.error) {
                          if (result.response.response == "success") {
                              $(btn).parent().parent().after(' <span class="label label-success url_validate_label"><i class="glyphicon glyphicon-ok icon-white"></i>' + i18n.t('validationMsgs.valid') + '</span>');

                          } else {
                              if (result.response.isConnectionError) {
                                if (result.response.response == null) {
                                    $(btn).parent().parent().after(' <span class="label label-danger url_validate_label"><i class="glyphicon glyphicon-remove icon-white"></i>' + i18n.t('validationMsgs.invalid') + '<br/>' + i18n.t('validationMsgs.errorInConnection') + '</span>');
                                } else { //When an exception is thrown from jsFunction_isURLValid
                                    $(btn).parent().parent().after(' <span class="label label-danger url_validate_label"><i class="glyphicon glyphicon-remove icon-white"></i>' + i18n.t('validationMsgs.invalid') + '</span>');
                                }
                              } else {
                                    if (result.response.statusCode == null) { //When an exception is thrown from sendHttpHEADRequest method
                                        $(btn).parent().parent().after(' <span class="label label-danger url_validate_label"><i class="glyphicon glyphicon-remove icon-white"></i>' + i18n.t('validationMsgs.invalid') + '<br/>' + result.response.response + '</span>');
                                    } else {
                                        $(btn).parent().parent().after(' <span class="label label-danger url_validate_label"><i class="glyphicon glyphicon-remove icon-white"></i>' + i18n.t('validationMsgs.invalid') + '<br/>' + result.response.statusCode + ' - ' + result.response.reasonPhrase + '</span>');
                                    }
                              }

                          }
                          var toFade = $(btn).parent().parent().find('.url_validate_label');
                          var foo = setTimeout(function() {
                                $(toFade).hide();
                          }, 3000);
                      }
                      $(btn).removeClass("loadingButton-small");
                      $(btn).val(i18n.t('validationMsgs.testUri'));
                  }, "json");

    });
});
