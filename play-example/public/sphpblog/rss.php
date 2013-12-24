<?php
	// RSS feed support
	// (c) 2004 Javier Gutiérrez Chamorro (Guti), guti <at> ya <dot> com
	//
	// Simple PHP Version: 0.3.7b
	// RSS Version:		0.3.7b

	// Include Required Functions
	require_once('scripts/sb_functions.php');

	// Please note, you can change the number of entries that appear by
	// passing the variable 'n' in the URL. You can also limit it to a certain
	// category by passing 'c'. For instance, this will make it show
	// 20 entries in the 'tech' category:
	// http://www.mywebsite.com/rss.php?n=20&c=tech

	// Output Page
	generate_rss( @$_GET[ 'n' ], @$_GET[ 'c' ] );
?>
