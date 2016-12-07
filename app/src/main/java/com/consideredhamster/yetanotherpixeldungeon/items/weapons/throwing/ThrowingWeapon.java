/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Yet Another Pixel Dungeon
 * Copyright (C) 2015-2016 Considered Hamster
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
package com.consideredhamster.yetanotherpixeldungeon.items.weapons.throwing;

import com.consideredhamster.yetanotherpixeldungeon.actors.mobs.Mob;
import com.consideredhamster.yetanotherpixeldungeon.ui.AttackIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.consideredhamster.yetanotherpixeldungeon.Assets;
import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.actors.Actor;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Confusion;
import com.consideredhamster.yetanotherpixeldungeon.actors.buffs.Invisibility;
import com.consideredhamster.yetanotherpixeldungeon.actors.hero.Hero;
import com.consideredhamster.yetanotherpixeldungeon.effects.Chains;
import com.consideredhamster.yetanotherpixeldungeon.items.Item;
import com.consideredhamster.yetanotherpixeldungeon.items.weapons.Weapon;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.mechanics.Ballistica;
import com.consideredhamster.yetanotherpixeldungeon.scenes.CellSelector;
import com.consideredhamster.yetanotherpixeldungeon.scenes.GameScene;
import com.consideredhamster.yetanotherpixeldungeon.sprites.ItemSpriteSheet;
import com.consideredhamster.yetanotherpixeldungeon.sprites.MissileSprite;
import com.consideredhamster.yetanotherpixeldungeon.ui.QuickSlot;
import com.consideredhamster.yetanotherpixeldungeon.utils.GLog;

public abstract class ThrowingWeapon extends Weapon {

//	private static final String TXT_MISSILES	= "Missile weapon";

//	private static final String TXT_YES			= "Yes, I know what I'm doing";
//	private static final String TXT_NO			= "No, I changed my mind";
//	private static final String TXT_R_U_SURE	=
//		"Do you really want to equip it as a melee weapon?";

    private static final String TXT_TARGET_CHARMED	= "You can't bring yourself to harm someone so... charming.";

    private static final String AC_SHOOT = "SHOOT";

    public ThrowingWeapon(int tier) {
        super();

        this.tier = tier;

        stackable = true;
    }

    @Override
    public String equipAction() {
        return AC_SHOOT;
    }

    @Override
    public String quickAction() {
        return isEquipped( Dungeon.hero ) ? AC_UNEQUIP : AC_EQUIP;
    }

    @Override
    public boolean isEquipped( Hero hero ) {
        return hero.belongings.weap2 != null && getClass().equals(hero.belongings.weap2.getClass());
    }

    @Override
    public int penaltyBase(Hero hero, int str) {

        return super.penaltyBase(hero, str) + tier * 2;

    }

    @Override
    public int lootChapter() {
        return tier;
    }

    @Override
    public int lootLevel() {
        return ( lootChapter() - 1 ) * 6 + 6 * quantity / baseAmount();
    }

    public int baseAmount() {
        return 1;
    }

    @Override
    public Item random() {
        quantity = Random.Int( baseAmount(), baseAmount() * 2);

        int delta = lootChapter() - Dungeon.chapter();

        if( delta > 0 ) {
            quantity = Math.max( 1, quantity / ( delta + 1 ) );
        }

        return this;
    }

    @Override
    public float breakingRateWhenShot() {
        return 0.05f;
    }
	
//	@Override
//	public ArrayList<String> actions( Hero hero ) {
//		ArrayList<String> actions = super.actions( hero );

//        actions.remove( AC_EQUIP );
//        actions.remove( AC_UNEQUIP );

//		return actions;
//	}

//	@Override
//	protected void onThrow( int cell ) {
//		Char enemy = Actor.findChar( cell );

//		if (enemy == null || enemy == curUser) {
//			super.onThrow( cell );
//		} else {
//			if (!curUser.shoot( enemy, this )) {
//				miss( cell );
//			}
//		}

//        if (enemy != null && enemy != curUser) {
//            curUser.shoot( enemy, this );
//		}
//
//        super.onThrow( cell );
//	}
	
//	protected void miss( int cell ) {
//		super.onThrow( cell );
//	}
	
//	@Override
//	public void proc( Char attacker, Char defender, int damage ) {
//
//		super.proc( attacker, defender, damage );
//
//		Hero hero = (Hero)attacker;
//		if (hero.rangedWeapon == null && stackable) {
//			if (quantity == 1) {
//				doUnequip( hero, false, false );
//			} else {
//				detach( null );
//			}
//		}
//	}

    @Override
    public boolean doEquip( final Hero hero ) {

        if( !this.isEquipped( hero ) ) {

            detachAll(hero.belongings.backpack);

            if( QuickSlot.quickslot1.value == getClass() && ( hero.belongings.weap2 == null || hero.belongings.weap2.bonus >= 0 ) )
                QuickSlot.quickslot1.value = hero.belongings.weap2 != null && hero.belongings.weap2.stackable ? hero.belongings.weap2.getClass() : hero.belongings.weap2 ;

            if( QuickSlot.quickslot2.value == getClass() && ( hero.belongings.weap2 == null || hero.belongings.weap2.bonus >= 0 ) )
                QuickSlot.quickslot2.value = hero.belongings.weap2 != null && hero.belongings.weap2.stackable ? hero.belongings.weap2.getClass() : hero.belongings.weap2 ;

//            if( QuickSlot.quickslot1.value == this && ( hero.belongings.weap2 == null || hero.belongings.weap2.bonus >= 0 ) )
//                QuickSlot.quickslot1.value = hero.belongings.weap2 != null && hero.belongings.weap2.stackable ? hero.belongings.weap2.getClass() : hero.belongings.weap2 ;
//
//            if( QuickSlot.quickslot2.value == this && ( hero.belongings.weap2 == null || hero.belongings.weap2.bonus >= 0 ) )
//                QuickSlot.quickslot2.value = hero.belongings.weap2 != null && hero.belongings.weap2.stackable ? hero.belongings.weap2.getClass() : hero.belongings.weap2 ;

            if (hero.belongings.weap2 == null || hero.belongings.weap2.doUnequip(hero, true, false)) {

                hero.belongings.weap2 = this;
                activate(hero);

                GLog.i(TXT_EQUIP, name());

                identify( CURSED_KNOWN );

                if (bonus < 0) {
                    equipCursed(hero);
                    GLog.n(TXT_EQUIP_CURSED, name());
                }

                QuickSlot.refresh();

                hero.spendAndNext(time2equip(hero));
                return true;

            } else {

                collect(hero.belongings.backpack);
                return false;

            }
        } else {

            GLog.w(TXT_ISEQUIPPED, name());
            return false;

        }
    }

    @Override
    public boolean doPickUp( Hero hero ) {

        Class<?>c = getClass();

        if (hero.belongings.weap2 != null && hero.belongings.weap2.getClass() == c) {

            hero.belongings.weap2.quantity += quantity;

            GameScene.pickUp( this );
            Sample.INSTANCE.play(Assets.SND_ITEM);

//            hero.spendAndNext(TIME_TO_PICK_UP);

            QuickSlot.refresh();

            return true;

        } else {

            return super.doPickUp(hero);

        }

    }

    @Override
    public String info() {

        final String p = "\n\n";

        int heroStr = Dungeon.hero.STR();
        int itemStr = strShown( isIdentified() );
        int penalty = GameMath.gate(0, penaltyBase(Dungeon.hero, strShown(isIdentified())), 20) * 5;
        float power = Math.max(0, isIdentified() ? (float)(min() + max()) / 2 : ((float)(min(0) + max(0)) / 2) );

        StringBuilder info = new StringBuilder( desc() );

        info.append( p );

        info.append( "This _tier-" + tier + " " + ( !descType().isEmpty() ? descType() + " " : "" )  + "weapon_ requires _" + itemStr + " points of strength_ to use effectively and" +
                ( isRepairable() ? ", given its _" + stateToString( state ) + " condition_, " : " " ) +
                "will deal _" + min() + "-" + max() + " points of damage_ per hit.");

        info.append( p );

        if (itemStr > heroStr) {
            info.append(
                    "Because of your inadequate strength, your stealth and accuracy with it " +
                            "will be _decreased by " + penalty + "%_ and attacking with it will be _" + (100 - 10000 / (100 + penalty)) + "% slower_." );
        } else if (itemStr < heroStr) {
            info.append(
                    "Because of your excess strength, your stealth and accuracy with it " +
                            "will " + ( penalty > 0 ? "be _decreased only by " + penalty + "%_" : "_not be decreased_" ) + " " +
                            "and attacking with it will deal additional _" + (float)(heroStr - itemStr) / 2 + " points of damage_." );
        } else {
            info.append(
                    "When wielding this weapon, your stealth and accuracy with it will " + ( penalty > 0 ? "be _decreased by " + penalty + "%_, " +
                            "but with additional strength this penalty can be reduced" : "_not be decreased_" ) + "." );
        }

        info.append( p );

        if (isEquipped( Dungeon.hero )) {

            info.append( "You hold these " + name + " at the ready." );

        } else if( Dungeon.hero.belongings.backpack.items.contains(this) ) {

            info.append( "These " + name + " are in your backpack. " );

        } else {

            info.append( "These " + name + " are on the dungeon's floor." );

        }

        return info.toString();
    }

    @Override
    public void execute( Hero hero, String action ) {

        if (action == AC_SHOOT) {

            curUser = hero;
            curItem = this;

            if (!isEquipped(hero)) {

                super.execute(hero, AC_THROW);

            } else {

                GameScene.selectCell( shooter );

            }


        } else {

            super.execute( hero, action );

        }
    }

    public static CellSelector.Listener shooter = new CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                final ThrowingWeapon curWeap = (ThrowingWeapon)ThrowingWeapon.curItem;

//                int tmp_cell = target;

                if( curUser.buff( Confusion.class ) != null ) {
                    target += Level.NEIGHBOURS8[Random.Int( 8 )];
                }

                final int cell = Ballistica.cast(curUser.pos, target, false, true);

                final Char ch = Actor.findChar( cell );

                if( ch != null && curUser != ch && Dungeon.visible[ cell ] ) {

                    if ( curUser.isCharmedBy( ch ) ) {
                        GLog.i( TXT_TARGET_CHARMED );
                        return;
                    }

                    QuickSlot.target(curItem, ch);
                    AttackIndicator.target( (Mob)ch );
                }



                curUser.sprite.cast(cell, new Callback() {
                    @Override
                    public void call() {

                        curUser.busy();

                        if (curWeap instanceof Harpoons) {
                            curUser.sprite.parent.add(new Chains(curUser.pos, cell, ch != null && ch.isHeavy()));
                        }

                        ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                        reset(curUser.pos, cell, curUser.belongings.weap2, new Callback() {
                            @Override
                            public void call() {
                                ((ThrowingWeapon) curUser.belongings.weap2).onShoot(cell, curWeap);
                            }
                        });
                    }
                });

                Invisibility.dispel();
                Sample.INSTANCE.play(Assets.SND_MISS, 0.6f, 0.6f, 1.5f);

            }
        }

        @Override
        public String prompt() {
            return "Choose target to throw at";
        }
    };

    public void onShoot( int cell, Weapon weapon ) {
        Char enemy = Actor.findChar(cell);

        // FIXME

        if( enemy == curUser ) {

            super.onThrow(cell);

        } else if( enemy == null || !curUser.shoot(enemy, weapon) ) {

            if (this instanceof Boomerangs || this instanceof Harpoons) {

                ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                        reset(cell, curUser.pos, this instanceof Harpoons ? ItemSpriteSheet.HARPOON_RETURN : curItem.imageAlt(), null);

                curUser.belongings.weap2 = this;

            } else {
                super.onThrow(cell);
            }

        } else if( Random.Float() > weapon.breakingRateWhenShot() ) {

            if (this instanceof Chakrams || this instanceof Harpoons) {

                if( !(this instanceof Harpoons) || !enemy.isHeavy()) {

                    ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                            reset(cell, curUser.pos, this instanceof Harpoons ? ItemSpriteSheet.HARPOON_RETURN : curItem.imageAlt(), null);

                }

                curUser.belongings.weap2 = this;

            } else {
                super.onThrow(cell);
            }

        } else {

            if (quantity == 1) {

                doUnequip( curUser, false, false );

            } else {

                detach( null );

            }
        }

        curUser.spendAndNext( curUser.attackDelay() );
        QuickSlot.refresh();
    }
}
