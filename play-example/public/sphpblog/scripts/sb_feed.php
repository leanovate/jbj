<?php

	// The Simple PHP Blog is released under the GNU Public License.
	//
	// You are free to use and modify the Simple PHP Blog. All changes 
	// must be uploaded to SourceForge.net under Simple PHP Blog or
	// emailed to apalmo <at> bigevilbrain <dot> com
	
	// -------------------
	// Blog Feed Functions
	// -------------------


	function clean_rss_output ( $str ) {
		// Decode/Encode HTML output
		global $lang_string, $blog_config;
		//$str = htmlspecialchars( $str, ENT_QUOTES, $lang_string[ 'php_charset' ] );
		
		return( $str );
	}


	function generate_feed ( $max_entries=0, $category='' )
	{
		// Read entries by month, year and/or day. Generate HTML output.
		//
		// Used for the main Index page.
		global $lang_string, $blog_config, $user_colors, $sb_info;
		
		// Read custom feed footer
		$content_footer=sb_read_file( 'interface/feed.xml' );
		if ( $content_footer==NULL ) {
			$content_footer='';
		}
		
		$entry_file_array = blog_entry_listing();

		// Read entry files
		if ( $max_entries<=0 ) {
			$max_entries=min( $blog_config->getTag('RSS_MAX_ENTRIES'), count( $entry_file_array ) );
		}
		else {
			$max_entries=min( $max_entries, count( $entry_file_array ) );
		}
		$entries=0;
		$i=0;
		$ret = array();
		while ( ( $entries<$max_entries ) && ( $i<count( $entry_file_array ) ) ) {
			list( $entry_filename, $year_dir, $month_dir ) = explode( '|', $entry_file_array[ $i ] );
			$contents=blog_entry_to_array( CONTENT_DIR . $year_dir . '/' . $month_dir . '/' . $entry_filename );
			$cats = explode( ',', $contents[ 'CATEGORIES' ] );
			for ( $j = 0; $j < count( $cats ); $j++ ) {
				if ( ( empty( $category ) ) || strpos( ',' . $category . ',', ',' . $cats[ $j ] . ',' )!==false ) {
					$entries++;
					$ret[] = array($year_dir, $month_dir, $entry_filename, $contents);
					break;
				}
			}
			$i++;
		}
		return $ret;
	}

	function generate_rss ( $max_entries=0, $category='' )
	{
		// Read entries by month, year and/or day. Generate HTML output.
		//
		// Used for the main Index page.
		global $lang_string, $blog_config, $user_colors, $sb_info;

		$base = sb_host() . BASEURL;
		
		header('Content-type: application/xml');
		echo "<?xml version=\"1.0\" encoding=\"" . $lang_string[ 'php_charset' ] . "\"?>\n";
		echo "<rss version=\"2.0\">\n";
		echo "\t<channel>\n";
		//Required channel fields
		echo "\t\t<title>" . clean_rss_output( $blog_config->getTag('BLOG_TITLE') ) . "</title>\n";
		echo "\t\t<link>" . $base . "index.php</link>\n";
		echo "\t\t<description><![CDATA[" . clean_rss_output( $blog_config->getTag('BLOG_FOOTER') ) . "]]></description>\n";
		// Read custom channel image
		if ( file_exists( 'interface/feed.png' ) ) {
			echo "\t\t<image>\n";
			echo "\t\t\t<url>" . $base . "interface/feed.png</url>\n";
			echo "\t\t\t<link>" . $base . "index.php</link>\n";
			echo "\t\t\t<title>" . clean_rss_output( $blog_config->getTag('BLOG_TITLE') ) . "</title>\n";
			echo "\t\t\t<description><![CDATA[" . clean_rss_output( $blog_config->getTag('BLOG_TITLE') ) . "]]></description>\n";
			echo "\t\t</image>\n";
		}
		//Optional channel fields
		echo "\t\t<copyright>" . clean_rss_output( 'Copyright ' . strftime( '%Y' ) . ', ' . $blog_config->getTag('BLOG_AUTHOR') ) . "</copyright>\n";
		echo "\t\t<managingEditor>" . $blog_config->getTag('BLOG_EMAIL') . " (" . $blog_config->getTag('BLOG_AUTHOR') . ")</managingEditor>\n";
		echo "\t\t<language>" . str_replace( '_', '-', $lang_string[ 'rss_locale' ] ) . "</language>\n";
		echo "\t\t<generator>SPHPBLOG " . $sb_info[ 'version' ] . "</generator>\n";

		$posts = generate_feed ( $max_entries, $category);

		for ($i=0;$i<count($posts); $i++) {
			$year_dir = $posts[$i][0];
			$month_dir = $posts[$i][1];
			$entry_filename = $posts[$i][2];
			$contents = $posts[$i][3];

			echo "\t\t<item>\n";
			//Required item fields
			echo "\t\t\t<title>" . clean_rss_output( blog_to_html( $contents[ 'SUBJECT' ], false, false ) ) . "</title>\n";
			echo "\t\t\t<link>" . $base . 'index.php?entry=' . sb_strip_extension( $entry_filename ) . "</link>\n"; /* Changed the link URL */
			echo "\t\t\t<description><![CDATA[" . clean_rss_output( replace_more_tag( blog_to_html( $contents[ 'CONTENT' ], false, false ), true, '' ) ) . $content_footer . "]]></description>\n";
					
			//Optional item fields
                        if (strlen($contents['CATEGORIES']) > 0) {
				$cats = explode( ',', $contents[ 'CATEGORIES' ] );
				echo "\t\t\t<category>";
				for ( $k = 0; $k < count( $cats ); $k++ ) {
					echo get_category_by_id( $cats[ $k ] );
					if ( $k < count( $cats ) - 1 ) {
						echo ', ';
					}
				}
				echo "</category>\n";
			}
			echo "\t\t\t<guid isPermaLink=\"true\">" . $base . 'index.php?entry=' . sb_strip_extension( $entry_filename ) . "</guid>\n"; /* Changed the guid URL */
			echo "\t\t\t<author>" . $blog_config->getTag('BLOG_EMAIL')  . " (" . $blog_config->getTag('BLOG_AUTHOR'). ")</author>\n";
			echo "\t\t\t<pubDate>" . gmdate( 'D, d M Y H:i:s', $contents[ 'DATE' ] ) . " GMT</pubDate>\n";

			// Only output if <comments> if they are enabled.
			if ( $blog_config->getTag('BLOG_ENABLE_COMMENTS') ) {
				echo "\t\t\t<comments>" . $base . 'comments.php?y=' . $year_dir . '&amp;m=' . $month_dir . '&amp;entry=' . sb_strip_extension( $entry_filename ) . "</comments>\n";
			}
			echo "\t\t</item>\n";
		}

		echo "\t</channel>\n";
		echo "</rss>\n";
	}

	function clean_rdf_output ( $str ) {
		// Decode/Encode HTML output
		global $lang_string, $blog_config;
		//$str = htmlspecialchars( $str, ENT_QUOTES, $lang_string[ 'php_charset' ] );

		return( $str );
	}


	function generate_rdf ( $max_entries=0, $category='' )
	{
		// Read entries by month, year and/or day. Generate HTML output.
		//
		// Used for the main Index page.
		global $lang_string, $blog_config, $user_colors, $sb_info;

		$base_url = sb_host() . BASEURL;

		header('Content-type: application/xml');
		echo "<?xml version=\"1.0\" encoding=\"" . $lang_string[ 'php_charset' ] . "\"?>\n";
		echo '<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:ref="http://purl.org/rss/1.0/modules/reference/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns="http://purl.org/rss/1.0/">' . "\n";
		echo "\t<channel rdf:about=\"" . $base_url . "rss.rdf\">\n";
		//Required channel fields
		echo "\t\t<title>" . clean_rdf_output( $blog_config->getTag('BLOG_TITLE') ) . "</title>\n";
		echo "\t\t<link>" . $base_url . "index.php</link>\n";
		echo "\t\t<description><![CDATA[" . clean_rdf_output( $blog_config->getTag('BLOG_FOOTER') ) . "]]></description>\n";
		//Optional channel fields
		// Read custom channel image
		if ( file_exists( 'interface/feed.png' ) ) {
			echo "\t\t<image rdf:resource=\"" . $base_url . "interface/feed.png\" />";
		}
		//echo "\t\t<copyright>" . clean_rdf_output( 'Copyright ' . strftime( '%Y' ) . ', ' . $blog_config->getTag('BLOG_AUTHOR') ) . "</copyright>\n";
		//echo "\t\t<managingEditor>" . clean_rdf_output($blog_config->getTag('BLOG_EMAIL') . ' (' . $blog_config->getTag('BLOG_AUTHOR') . ')' ) . "</managingEditor>\n";
		//echo "\t\t<language>" . str_replace( '_', '-', $lang_string[ 'rss_locale' ] ) . "</language>\n";
		//echo "\t\t<generator>SPHPBLOG " . $sb_info[ 'version' ] . "</generator>\n";


		echo "\t\t<items>\n";
		echo "\t\t\t<rdf:Seq>\n";

                $posts = generate_feed ( $max_entries, $category);

                for ($i=0;$i<count($posts); $i++) {
                        $entry_filename = $posts[$i][2];
			echo "\t\t\t\t<rdf:li resource=\"" . $base_url . 'index.php?entry=' . sb_strip_extension( $entry_filename ) . "\" />\n";
		}

		echo "\t\t\t</rdf:Seq>\n";
		echo "\t\t</items>\n";
		echo "\t</channel>\n";


                for ($i=0;$i<count($posts); $i++) {
                        $year_dir = $posts[$i][0];
                        $month_dir = $posts[$i][1];
                        $entry_filename = $posts[$i][2];
                        $contents = $posts[$i][3];

			echo "\t<item rdf:about=\"" . $base_url . 'index.php?entry=' . sb_strip_extension( $entry_filename ) . "\">\n";
			//Required item fields
			echo "\t\t<title>" . clean_rdf_output( blog_to_html( $contents[ 'SUBJECT' ], false, false ) ) . "</title>\n";
			echo "\t\t<link>" . $base_url . 'index.php?entry=' . sb_strip_extension( $entry_filename ) . "</link>\n"; /* Changed the link URL */
			echo "\t\t<description><![CDATA[" . clean_rdf_output( replace_more_tag( blog_to_html( $contents[ 'CONTENT' ], false, false ), true, '' ) ) . $content_footer . "]]></description>\n";
			
			//Optional item fields
			//echo "\t\t<guid isPermaLink=\"true\">" . $base_url . 'index.php?entry=' . sb_strip_extension( $entry_filename ) . "</guid>\n"; /* Changed the guid URL */
			//echo "\t\t<author>" . clean_rdf_output( $blog_config->getTag('BLOG_EMAIL') ) . "</author>\n";
			//echo "\t\t<pubDate>" . gmdate( 'D, d M Y H:i:s', $contents[ 'DATE' ] ) . " GMT</pubDate>\n";
			echo "\t</item>\n";
		}
		echo "</rdf:RDF>\n";
	}


	function clean_atom_output ( $str ) {
		// Decode/Encode HTML output
		global $lang_string, $blog_config;
		// $str = htmlspecialchars( $str, ENT_QUOTES, $lang_string[ 'php_charset' ] );

		return( $str );
	}

	function generate_atom ( $max_entries=0, $category='' )
	{
		// Read entries by month, year and/or day. Generate HTML output.
		//
		// Used for the main Index page.
		global $lang_string, $blog_config, $user_colors, $sb_info;

		$base_url = sb_host() . BASEURL;

		header('Content-type: application/xml');
		echo "<?xml version=\"1.0\" encoding=\"" . $lang_string[ 'php_charset' ] . "\"?>\n";
		echo "<feed xmlns=\"http://www.w3.org/2005/Atom\">\n";
		echo "\t<title>" . clean_atom_output( $blog_config->getTag('BLOG_TITLE')) . "</title>\n";
		echo "\t<link href=\"" . $base_url . "atom.php\" rel=\"self\" />\n";
		echo "\t<link href=\"" . $base_url . "\" />\n"; echo "\t<id>". $base_url . "index.php</id>\n";
		echo "\t<updated>" . gmdate( 'Y-m-d' ) . 'T' . gmdate( 'H:i:s' ) . "Z</updated>\n";
		echo "\t<author>\n"; echo "\t\t<name>" . clean_atom_output( $blog_config->getTag('BLOG_EMAIL')) . "</name>\n";
		echo "\t\t<email>" . clean_atom_output( $blog_config->getTag('BLOG_EMAIL')) . "</email>\n";
		echo "\t</author>\n";

                $posts = generate_feed ( $max_entries, $category);

                for ($i=0;$i<count($posts); $i++) {
                        $year_dir = $posts[$i][0];
                        $month_dir = $posts[$i][1];
                        $entry_filename = $posts[$i][2];
                        $contents = $posts[$i][3];

			echo "\t<entry>\n"; echo "\t\t<title>" . clean_atom_output( blog_to_html( $contents[ 'SUBJECT' ], false, false ) ) . "</title>\n";
			echo "\t\t<link href=\"" . $base_url . "index.php?entry=" . sb_strip_extension( $entry_filename ) . "\" />\n";
			echo "\t\t<link rel=\"alternate\" type=\"text/html\" href=\"" . $base_url . "index.php?entry=" . sb_strip_extension( $entry_filename ) . "\" />\n";
			echo "\t\t<link rel=\"edit\" href=\"" . $base_url . "index.php?entry=" . sb_strip_extension( $entry_filename ) . "\" />\n"; echo "\t\t<id>" . $base_url . "index.php?entry=" . sb_strip_extension( $entry_filename ) . "</id>\n";
			echo "\t\t<summary type=\"html\"><![CDATA[" . replace_more_tag( blog_to_html( $contents[ 'CONTENT' ], false, false ), true, '' ) . $content_footer . "]]></summary>\n";
			echo "\t\t<updated>" . gmdate( 'Y-m-d', $contents[ 'DATE' ] ) . 'T' . gmdate( 'H:i:s', $contents[ 'DATE' ] ) . "Z</updated>\n";
			echo "\t</entry>\n"; 

		}
		echo "</feed>\n";
	}

?>
