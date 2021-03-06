/**
** <JNDfaDeltaPainter.java> -- The NDFA's  specific delta painter
**
** Copyright (C) 2002 by  Ivan Hernández Serrano
**
** Copyright (C) 2002 by  Ivan Hernández Serrano
**
** This file is part of JAGUAR
**
** This program is free software; you can redistribute it and/or
** modify it under the terms of the GNU General Public License
** as published by the Free Software Foundation; either version 2
** of the License, or (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
**
** Author: Ivan Hernández Serrano <ivanx@users.sourceforge.net>
**
**/


package jaguar.machine.dfa.jdfa.jstructures;

import jaguar.machine.util.jutil.JDeltaPainter;
import jaguar.machine.util.jutil.JDeltaGraphic;
import java.awt.*;
import java.awt.geom.*;
import jaguar.structures.*;
import jaguar.machine.structures.*;
import jaguar.structures.jstructures.*;
import java.util.*;

/**
 * Pinta una NdfaDelta
 *
 * @author Ivan Hernández Serrano <ivanx@users.sourceforge.net>
 * @version 0.1
 */
public class JNDfaDeltaPainter extends JDeltaPainter {
    /**
     * Constructor sin parámetros.
     * Inicializa el objeto usando los valores por omision.
     */
    public JNDfaDeltaPainter (){super();}


    /**
     * Constructor sin parámetros.
     * Inicializa el objeto usando los valores por omision.
     */
    public JNDfaDeltaPainter (JDeltaGraphic _jdelta){
        jdelta = _jdelta;
    }


    public void paint(Graphics g){
        Hashtable<Point,Hashtable<Point,Boolean>> centrosp1 = new Hashtable<Point,Hashtable<Point,Boolean>>();
        Hashtable<Point,Boolean> currentp, htmp;
        HashSet<State> currentDestinos;
        JState jq, jp;
        Point x,y;
        boolean first_check ;
        // las transiciones van de q -> p
        Vector vaux, vpair;
        g.setFont(ARROW_LABEL_FONT);
        // Este for nos va a dar los estados a partir de donde salen las transiciones,
        // i.e. este enumeration nos da los estados de los cuales podemos hacer transiciones
        // con algun simbolo...
        for(Enumeration e = ((Delta)jdelta).keys();  e.hasMoreElements() ;) {
            g.setColor(ARROW_COLOR);
            first_check = false;
            // Ahora para cada estado de estos tenemos que sacar todas sus transiciones
            JState q = (JState)e.nextElement();
            vaux = ((Delta)jdelta).getTransitions(((State) q));
            currentDestinos = new HashSet<State>();

            for(int i = 0 ; i < vaux.size(); i++) {
                vpair = (Vector)vaux.elementAt(i);
                Symbol sym = (Symbol)vpair.elementAt(0);
                State p;

                Object o[] = ((StateSet)vpair.elementAt(1)).toArray();
                for(int k = 0;  k < o.length ; k++) {
                    p = (State)o[k];
                    if (!currentDestinos.contains(p)) {
                        Graphics2D g2d = (Graphics2D) g;
                        Stroke origS = g2d.getStroke();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                        if(q.equals(jdelta.getCurrent_p()) && p.equals(jdelta.getCurrent_q()) && sym.equals(jdelta.getCurrent_sym())) {
                            g.setColor(CURRENT_TRANSITION_COLOR);
                        }
                        g2d.setStroke(new BasicStroke(2.0f));
                        if (q.equals(p)) {
                            if (q.equals(jdelta.getCurrent_p()) && p.equals(jdelta.getCurrent_q()))
                                g.setColor(CURRENT_TRANSITION_COLOR);
                            Point c = ((JState)p).getCentro();
                            java.awt.geom.CubicCurve2D.Float curve = new java.awt.geom.CubicCurve2D.Float(
                                          (float)c.getX()-5, (float)c.getY(), (float)(c.getX()-JState.DIAMETRO/1.5), (float)(c.getY() - JState.DIAMETRO*1.4),
                                          (float)(c.getX()+JState.DIAMETRO/1.5), (float)(c.getY() - JState.DIAMETRO*1.4), (float)c.getX()+5, (float)c.getY());

                            g2d.draw(curve);
                            paintLabelSelfState(g2d, ((JState)q).getQuadCurveCP(), jdelta.getLabelString((JState)q,(JState)p));
                            paintArrowHead(g2d,new Point2D.Double(c.getX()+JState.DIAMETRO/3.5,c.getY() - JState.DIAMETRO*1.4),
                                                   new Point2D.Double(c.getX()+10, c.getY()), g.getColor(), first_check);
                        } else {
                            java.awt.geom.Line2D.Double l2d = new java.awt.geom.Line2D.Double();
                            jq = (JState) q;
                            jp = (JState) p;
                            x = jq.getCentro().getLocation();
                            y = jp.getCentro().getLocation();
                            if ((currentp = centrosp1.get(x)) != null) {
                                if (currentp.get(y) !=null) {
                                    first_check = true;
                                } else {
                                    currentp.put(y,new Boolean(true));
                                }
                            } if (!first_check) {
                                x = jq.getCentro().getLocation();
                                y = jp.getCentro().getLocation();
                                if ((currentp = centrosp1.get(y)) != null) {
                                    if (currentp.get(y) !=null) {
                                        x.translate(10,10);
                                        y.translate(10,10);
                                        first_check = true;
                                    } else {
                                        currentp.put(x,new Boolean(true));
                                    }
                                } else {
                                    htmp = new Hashtable<Point,Boolean>();
                                    htmp.put(x,new Boolean(true));
                                    centrosp1.put(y,htmp);
                                }
                            }
                            l2d.setLine(x,y);
                            g2d.draw(l2d);
                            paintArrowHead(g2d,x,y,ARROW_COLOR, false);
                            paintLabel(g2d,x,y, jdelta.getLabelString((JState)q,(JState)p), false);
                        }
                        g2d.setStroke(origS);
                    }
                }
            }
        }
    }

}

/* JNDfaDeltaPainter.java ends here. */
