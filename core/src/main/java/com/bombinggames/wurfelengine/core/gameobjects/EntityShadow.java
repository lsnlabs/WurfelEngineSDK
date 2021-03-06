/*
 * Copyright 2013 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 * A simple drop shadow which drops straight to the ground.
 * @author Benedikt Vogler
 */
public class EntityShadow extends AbstractEntity implements Component {

	private static final long serialVersionUID = 1L;
	/**
	 * the parent class. The object where this is the shadow
	 */
	private AbstractEntity character;

	/**
	 *
	 */
	public EntityShadow() {
		super((byte) 6);
		this.setName("Shadow");
	}

	@Override
	public void update(float dt) {
		setSavePersistent(false);
		if (character == null || !character.hasPosition()) {
			dispose();
		} else if (character.isHidden())
			setHidden(true);
		else {
			//spawn if needed
			if (!hasPosition()) {
				spawn(character.getPoint().cpy());
			}
			//find height of shadow surface
			getPoint().set(character.getPosition());//start at character
			while (getPoint().getZ() > 0
				&& (RenderCell.isTransparent(getPoint().getBlock()))
			) {
				getPoint().add(0, 0, -RenderCell.GAME_EDGELENGTH);
			}
			//last level
			if (character.getPosition().getZPoint() < RenderCell.GAME_EDGELENGTH) {
				getPoint().setZ(0);
			} else {
				getPoint().setZ((getPoint().getZGrid() + 1) * RenderCell.GAME_EDGELENGTH);
			}
			setHidden(character.getPosition().getZPoint() < getPoint().z);
		}
	}

	@Override
	public void render(GameView view) {
		if (character == null || !character.hasPosition() || !hasPosition() || character.isHidden()) {
			dispose();
		} else {
			getColor().set(
					.5f,
					.5f,
					.5f,
					1 - (character.getPosition().getZ() - getPosition().getZ()) / 2 / RenderCell.GAME_EDGELENGTH+0.1f
			);
			super.render(view);
			//always visible smaller shadow
			if (getColor().a < 0.9f) {//render only if small shadow would be visible
				getColor().a = 0.2f;
				float prevScale = getScaling();
				setScaling(0.5f);
				super.render(view);
				setScaling(prevScale);
			}
		}
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}

	/**
	 *
	 * @param body
	 */
	@Override
	public void setParent(AbstractEntity body) {
		this.character = body;
	}

	@Override
	public void dispose() {
		character.removeComponent(this);
		super.dispose();
	}

}
