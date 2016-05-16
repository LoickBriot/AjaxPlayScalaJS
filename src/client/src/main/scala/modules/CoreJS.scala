package modules

import org.scalajs.jquery.jQuery

object CoreJS {

  
  def include(script: String) {
    jQuery(s"""
     <script type="text/javascript" >
  		  $$(function() {
  			  $$.ajax({
  				url : '$script',
  				dataType : "script",
  				async : false,
  				success : function() {
  				},
  				error : function() {
  					throw new Error("Could not load script "
  							+ script);
  				}
  			});
			});
			</script>
			""").appendTo(jQuery(".scalaJS"))
  }

}
