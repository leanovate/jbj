<?php
	/**
	* Archive tree view.
	*
	* Alexander Palmo <apalmo at bigevilbrain dot com>
	*/
	
	class Badges extends Sidebar {
		var $plugin;
		
		/* ------ INITIALIZE ------ */
		
		function Badges () {
			$this->plugin = 'Badges';
			$this->loadPrefs();
		}

                /* ------ PREFERENCES ------ */

                function defaultPrefs () {
                        $arr = parent::defaultPrefs();
                        $arr['sphpblog'] = 'on';
                        $arr['php'] = 'on';
                        $arr['text'] = 'on';
                        $arr['rss'] = 'on';
                        $arr['atom'] = 'on';
                        $arr['rdf'] = 'on';

                        return $arr;
                }

                /* ------ OPTIONS ------ */

                function optionsForm () {
			$checked = 'checked="checked"';

                        $sphpblog = $this->prefs['sphpblog'];
                        $php = $this->prefs['php'];
                        $text = $this->prefs['text'];
                        $rss = $this->prefs['rss'];
                        $atom = $this->prefs['atom'];
                        $rdf = $this->prefs['rdf'];

                        $save = _sb('submit_btn');

                        ob_start(); ?>
                        <!-- FORM -->
			<p>Powered By</p>
                        <form method="post" onsubmit="return validate(this)">
				<input type="checkbox" name="sphpblog" <?php if (!empty($sphpblog)) { echo $checked; } ?>>
                                <label for="sphpblog">Simple PHP Blog</label><br />
				<input type="checkbox" name="php" <?php if (!empty($php)) { echo $checked; } ?>>
                                <label for="php">PHP</label><br />
				<input type="checkbox" name="text" <?php if (!empty($text)) { echo $checked; } ?>>
                                <label for="text">Text</label><br />
				<input type="checkbox" name="rss" <?php if (!empty($rss)) { echo $checked; } ?>>
                                <label for="rss">RSS</label><br />
				<input type="checkbox" name="atom" <?php if (!empty($atom)) { echo $checked; } ?>>
                                <label for="atom">ATOM</label><br />
				<input type="checkbox" name="rdf" <?php if (!empty($rdf)) { echo $checked; } ?>>
                                <label for="rdf">RDF</label><br />

                                <input type="submit" name="save" value="<?php echo( $save ); ?>" />
                        </form>
                        <?php
                        $str = ob_get_clean();

                        return $str;
                }

                function optionsPost () {
                        if ( array_key_exists( 'save', $_POST ) ) {

				$value = '';
                                if ( array_key_exists( 'sphpblog', $_POST ) ) {
                                        $value = 'on';
                                }
                                $this->prefs['sphpblog'] = $value;

				$value = '';
                                if ( array_key_exists( 'text', $_POST ) ) {
                                        $value = 'on';
                                }
                                $this->prefs['text'] = $value;

				$value = '';
                                if ( array_key_exists( 'php', $_POST ) ) {
                                        $value = 'on';
                                }
                                $this->prefs['php'] = $value;

				$value = '';
                                if ( array_key_exists( 'rss', $_POST ) ) {
                                        $value = 'on';
                                }
                                $this->prefs['rss'] = $value;

				$value = '';
                                if ( array_key_exists( 'atom', $_POST ) ) {
                                        $value = 'on';
                                }
                                $this->prefs['atom'] = $value;

				$value = '';
                                if ( array_key_exists( 'rdf', $_POST ) ) {
                                        $value = 'on';
                                }
                                $this->prefs['rdf'] = $value;

                                $this->savePrefs();

                        }
                }

		
		/* ------ GETTERS & SETTERS ------ */
		
		function getTitle () {
			return 'Powered By';
		}
		
		function getContent () {
    // WEB BADGES
    $html = '<div style="text-align: center">' . "\n";
    if (!empty($this->prefs['sphpblog'])) 
    $html .= '<a href="http://sourceforge.net/projects/sphpblog/"><img height="15" width="80" style="margin-bottom: 5px;" src="interface/button_sphpblog.png" alt="Powered by Simple PHP Blog" title="Powered by Simple PHP Blog" /></a><br/>';
    if (!empty($this->prefs['php'])) 
    $html .= '<a href="http://php.net/"><img height="15" width="80" style="margin-bottom: 5px;" src="interface/button_php.png" alt="Powered by PHP ' . phpversion() . '" title="Powered by PHP ' . phpversion() . '" /></a><br />';
    if (!empty($this->prefs['text'])) 
    $html .= '<img height="15" width="80" style="margin-bottom: 5px;" src="interface/button_txt.png" alt="Powered by Plain text files" title="Powered by Plain Text Files" /><br />';
    if (!empty($this->prefs['rss'])) 
    $html .= '<a href="rss.php"><img height="15" width="80" style="margin-bottom: 5px;" src="interface/button_rss20.png" alt="Get RSS 2.0 Feed" title="Get RSS 2.0 Feed" /></a><br />';
    if (!empty($this->prefs['atom'])) 
    $html .= '<a href="atom.php"><img height="15" width="80" style="margin-bottom: 5px;" src="interface/button_atom03.png" alt="Get Atom 0.3 Feed" title="Get Atom 0.3 Feed" /></a><br />' ;
    if (!empty($this->prefs['rdf'])) 
    $html .= '<a href="rdf.php"><img height="15" width="80" style="margin-bottom: 5px;" src="interface/button_rdf10.png" alt="Get RDF 1.0 Feed" title="Get RDF 1.0 Feed" /></a><br />';
    $html .= '</div>' . "\n";
			return $html;
		}
	}
?>
