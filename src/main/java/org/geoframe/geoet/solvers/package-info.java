/*
 * GNU GPL v3 License
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * GEOET components directly usable in simulation environments.
 * <p>
 * The actual numerics live in kernels under {@link org.geoframe.geoet.core}
 * ({@code penmanmonteithfao}, {@code priestleytaylor},
 * {@code transpiration}, {@code radiation}, {@code stressfactor}, {@code rootdensity}) and
 * are not meant to be picked directly.
 * <p>
 * Class names follow a fixed grammar, so the name alone tells you what a solver does:
 * <pre>
 *     &lt;Equation&gt;Solver(With&lt;Feature&gt;)*
 * </pre>
 * <ul>
 *   <li><b>{@code <Equation>}</b> &mdash; the base equation/process being solved, e.g.
 *       {@code PenmanMonteithFAO}, {@code PriestleyTaylor}, {@code Prospero}</li>
 *   <li><b>{@code Solver}</b> &mdash; always present immediately after the equation name.</li>
 *   <li><b>{@code With<Feature>}</b> &mdash; zero or more optional variants, each appended
 *       independently, e.g. {@code WithStressFactor} (an externally supplied stress
 *       multiplier instead of none/self-computed), {@code WithCanopy} (radiation reaching
 *       the soil under a canopy instead of station-measured net radiation),
 *       {@code WithExplicitLAI} (canopy quantities staged by leaf-area index). A bare name
 *       with no {@code With} clause is the plain base case.</li>
 * </ul>
 * For example, {@code PenmanMonteithFAOSolverWithStressFactor} solves the FAO
 * Penman-Monteith evapotranspiration equation driven by an externally computed stress
 * factor, as opposed to the bare {@code PenmanMonteithFAOSolver} (no stress at all, i.e.
 * potential ET) or {@code PenmanMonteithFAOSolverWithFAOWaterStress} (which computes its
 * own FAO water-balance stress internally).
 * <p>
 * 
 * <p>The four {@code Jarvis*} stress-factor
 * solvers and {@code RadiationSolverComplete} currently have no test coverage exercising
 * them -- treat them as unverified until a test is written.
 */
package org.geoframe.geoet.solvers;
