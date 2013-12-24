<?php
	/**
	* Archive tree view.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Bookmarks extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Bookmarks () {
			$this->plugin = 'Bookmarks';
			$this->loadPrefs();
		}
		
                /* ------ PREFERENCES ------ */

                function defaultPrefs () {
                        $arr = parent::defaultPrefs();
                        $arr['provider'] = 'addthis';

                        return $arr;
                }

                /* ------ OPTIONS ------ */

                function optionsForm () {
                        $provider = $this->prefs['provider'];
			$checked = "checked='checked'";

                        $save = _sb('submit_btn');

                        ob_start(); ?>
                        <!-- FORM -->
                        <form method="post" onsubmit="return validate(this)">
                                <label for="provider">Bookmark Provider:</label><p>
				<input type="radio" name="provider" value="addthis" <?php if ($provider == 'addthis') { echo $checked; }  ?>> AddThis
				<input type="radio" name="provider" value="addtoany" <?php if ($provider == 'addtoany') { echo $checked; }  ?>> AddToAny
				<input type="radio" name="provider" value="socialmarker" <?php if ($provider == 'socialmarker') { echo $checked; }  ?>> Social Marker</p>
                                <input type="submit" name="save" value="<?php echo( $save ); ?>" />
                        </form>
                        <?php
                        $str = ob_get_clean();

                        return $str;
                }

                function optionsPost () {
                        if ( array_key_exists( 'save', $_POST ) ) {

                                if ( array_key_exists( 'provider', $_POST ) ) {
                                        $provider = sb_stripslashes( $_POST['provider'] );
                                                $this->prefs['provider'] = $provider;
                                                $this->savePrefs();
                                }
                        }
                }


		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return _sb('Bookmark Sharing');
		}
		
		function getContent () {
			$addthis = '<!-- AddThis Button BEGIN -->
<a class="addthis_button" href="http://www.addthis.com/bookmark.php?v=250&amp;username=xa-4c697ff844cb6c4f"><img src="http://s7.addthis.com/static/btn/sm-share-en.gif" width="83" height="16" alt="Bookmark and Share" style="border:0"/></a><script type="text/javascript" src="http://s7.addthis.com/js/250/addthis_widget.js#username=xa-4c697ff844cb6c4f"></script>
<!-- AddThis Button END -->
';

if( $this->prefs['provider'] == 'addtoany' ) {
			return '<!-- AddToAny BEGIN -->
<a class="a2a_dd" href="http://www.addtoany.com/share_save"><img src="http://static.addtoany.com/buttons/share_save_106_16.gif" width="106" height="16" alt="Share/Bookmark"/></a>
<script type="text/javascript" src="http://static.addtoany.com/menu/page.js"></script>
<!-- AddToAny END -->';
}

if( $this->prefs['provider'] == 'socialmarker' ) {
			return '<a href="javascript:window.location = \'http://www.socialmarker.com/?link=\'+encodeURIComponent (location.href)+\'&title=\'+encodeURIComponent( document.title);"><img src= "http://www.socialmarker.com/bookmark.gif" border="0" alt="share" /></a><a href="http://www.ascuns.ro/">Detect yahoo invisible</a>';
}
			return $addthis;

		}
	}
?>
