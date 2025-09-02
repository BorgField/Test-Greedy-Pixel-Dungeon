/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.custom.messages.M;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndChallenges extends WndTabbed {

	private static final int WIDTH		= 120;
	private static final int HEIGHT 	= 152;
	private static final int TTL_HEIGHT = 16;
	private static final int BTN_HEIGHT = 16;
	private static final int GAP        = 1;

	private ChallengeTab challenge;
	private DlcTab dlc;

	//	private static boolean editable;
	static boolean editable;
	private ArrayList<CanScrollCheckBox> boxes;
	private ArrayList<CanScrollCheckBox> dlcBoxes;
	private ArrayList<CanScrollInfo> infos;

	public static int lastIdx = 0;

	public WndChallenges( int checked, boolean editable ) {
		super();
		resize( WIDTH, HEIGHT);
		WndChallenges.editable = editable;

		this.boxes = new ArrayList<>();
		this.dlcBoxes = new ArrayList<>();
		this.infos = new ArrayList<>();

		challenge = new ChallengeTab(checked);
		add(challenge);
		challenge.setRect(0, 0, WIDTH, HEIGHT);
		dlc = new DlcTab(checked);
		add(dlc);
		dlc.setRect(0, 0, WIDTH, HEIGHT);

		add(new IconTab(Icons.get(Icons.INFO)) {
			@Override
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 0;
				challenge.visible = challenge.active = selected;
			}
		});

		add(new IconTab(Icons.get(Icons.BUFFS)) {
			@Override
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 1;
				dlc.visible = dlc.active = selected;
			}
		});
		layoutTabs();
		select(0);
	}

	private class ChallengeTab extends Component {
		private ScrollPane scrollPane;
		private Component content;


		@Override
		protected void createChildren() {
			super.createChildren();
			scrollPane = new ScrollPane(content = new Component()){
				@Override
				public void onClick(float x, float y) {
					int max_size = boxes.size();
					for (int i = 0; i < max_size; ++i) {
						if (boxes.get(i).onClick(x, y))
							return;
					}
					max_size = infos.size();
					for(int i = 0; i<max_size;++i){
						if(infos.get(i).onClick(x,y)){
							return;
						}
					}
				}
			};
			add(scrollPane);
			scrollPane.scrollTo(0, 0);
		}
		ChallengeTab(int checked) {
			super();
			createChildren();
			// 标题和普通挑战布局（同原WndChallenges）
			RenderedTextBlock title = PixelScene.renderTextBlock( M.L(Challenges.class, "traditional"), 11 );
			title.hardlight( TITLE_COLOR );
			title.setPos(
					(WIDTH - title.width()) / 2,
					(TTL_HEIGHT - title.height()) / 2 - 2
			);
			content.add( title );

//			boxes = new ArrayList<>();
			float pos = TTL_HEIGHT;
			for (int i=0; i < Challenges.NAME_IDS.length; i++) {
				if (i == 9) {break;}

				final String challenge = Challenges.NAME_IDS[i];

				CanScrollCheckBox cb = new CanScrollCheckBox( Messages.titleCase(Messages.get(Challenges.class, challenge)) );
				cb.checked( (checked & Challenges.MASKS[i]) != 0 );
				cb.active = editable;

				if (i > 0) {
					pos += GAP;
				}
				cb.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

				content.add( cb );
				WndChallenges.this.boxes.add( cb );

				IconButton info = new IconButton(Icons.get(Icons.INFO)){
					@Override
					protected void onClick() {
						super.onClick();
						ShatteredPixelDungeon.scene().add(
								new WndMessage(Messages.get(Challenges.class, challenge+"_desc"))
						);
					}
				};
				info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
				content.add(info);

				pos = cb.bottom();
				content.setSize(width, (int) pos + 6);
			}
		}

		@Override
		protected void layout() {
			super.layout();
			scrollPane.setRect(0, 0, width, height);
		}
	}
	private class DlcTab extends Component {
		private ScrollPane scrollPane;
		private Component content;

		@Override
		protected void createChildren() {
			super.createChildren();
			scrollPane = new ScrollPane(content = new Component()){
				@Override
				public void onClick(float x, float y) {
					int max_size = dlcBoxes.size();
					for (int i = 0; i < max_size; ++i) {
						if (dlcBoxes.get(i).onClick(x, y))
							return;
					}
					max_size = infos.size();
					for(int i = 0; i<max_size;++i){
						if(infos.get(i).onClick(x,y)){
							return;
						}
					}
				}
			};
			add(scrollPane);
			scrollPane.scrollTo(0, 0);
		}

		DlcTab(int checked) {
			super();
			createChildren();
			// 标题和普通挑战布局（同原WndChallenges）
			RenderedTextBlock title = PixelScene.renderTextBlock( M.L(Challenges.class, "expansion"), 11 );
			title.hardlight( TITLE_COLOR );
			title.setPos(
					(WIDTH - title.width()) / 2,
					(TTL_HEIGHT - title.height()) / 2 - 2
			);
			content.add( title );

//			dlcBoxes = new ArrayList<>();
			float pos = TTL_HEIGHT;
			for (int i = 9; i < Challenges.NAME_IDS.length; i++) {

				final String challenge = Challenges.NAME_IDS[i];

				CanScrollCheckBox cb = new CanScrollCheckBox( Messages.titleCase(Messages.get(Challenges.class, challenge)) );
				cb.checked( (checked & Challenges.MASKS[i]) != 0 );
				cb.active = WndChallenges.this.editable;

				if (i > 9) {
					pos += GAP;
				}
				cb.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

				content.add( cb );
				WndChallenges.this.dlcBoxes.add( cb );

				IconButton info = new IconButton(Icons.get(Icons.INFO)){
					@Override
					protected void onClick() {
						super.onClick();
						ShatteredPixelDungeon.scene().add(
								new WndMessage(Messages.get(Challenges.class, challenge+"_desc"))
						);
					}
				};
				info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
				content.add(info);

				pos = cb.bottom();
				content.setSize(width, (int) pos + 6);
			}
		}

		@Override
		protected void layout() {
			super.layout();
			scrollPane.setRect(0, 0, width, height);
		}
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			int value = 0;
			// 合并普通挑战
			for (int i=0; i < boxes.size(); i++) {
				if (boxes.get(i).checked()) {
					value |= Challenges.MASKS[i];
				}
			}
			// 合并DLC挑战（从索引9开始）
			for (int i=0; i < dlcBoxes.size(); i++) {
				if (dlcBoxes.get(i).checked()) {
					value |= Challenges.MASKS[i + 9];
				}
			}
			SPDSettings.challenges(value);
		}

		super.onBackPressed();
	}

	public static class CanScrollCheckBox extends CheckBox{

		public CanScrollCheckBox(String label) {
			super(label);
		}

		protected boolean onClick(float x, float y){
			if(!inside(x,y)) return false;
			if(active) onClick();

			return true;
		}

		@Override
		protected void layout(){
			super.layout();
			hotArea.width = hotArea.height = 0;
		}
	}

	public static class CanScrollInfo extends IconButton{
		public CanScrollInfo(Image Icon){super(Icon);}

		protected boolean onClick(float x, float y){
			if(!inside(x,y)) return false;
			if(active) onClick();
			return true;
		}

		@Override
		protected void layout(){
			super.layout();
			hotArea.width = hotArea.height = 0;
		}
	}
}