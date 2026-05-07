import SwiftUI

/// Canvas view for rendering the starry sky
struct SkyCanvasView: View {
    let stars: [StarViewModel]
    let gradient: [Color]

    var body: some View {
        GeometryReader { geometry in
            ZStack {
                // Background gradient
                LinearGradient(
                    gradient: Gradient(colors: gradient),
                    startPoint: .bottom,
                    endPoint: .top
                )
                .ignoresSafeArea()

                // Stars canvas
                Canvas { context, size in
                    for star in stars {
                        // Only draw stars within the visible area
                        guard star.x >= 0 && star.x <= size.width &&
                              star.y >= 0 && star.y <= size.height else {
                            continue
                        }

                        // Draw glow effect for bright stars
                        if star.brightness > 0.3 {
                            let glowRadius = star.size * 2.0
                            let glowPath = Path(
                                ellipseIn: CGRect(
                                    x: star.x - glowRadius / 2,
                                    y: star.y - glowRadius / 2,
                                    width: glowRadius,
                                    height: glowRadius
                                )
                            )
                            context.fill(
                                glowPath,
                                with: .color(star.color.opacity(star.brightness * 0.3))
                            )
                        }

                        // Draw the star
                        let starPath = Path(
                            ellipseIn: CGRect(
                                x: star.x - star.size / 2,
                                y: star.y - star.size / 2,
                                width: star.size,
                                height: star.size
                            )
                        )
                        context.fill(starPath, with: .color(star.color))

                        // Add extra bright core for very bright stars
                        if star.brightness > 0.7 {
                            let coreSize = star.size * 0.5
                            let corePath = Path(
                                ellipseIn: CGRect(
                                    x: star.x - coreSize / 2,
                                    y: star.y - coreSize / 2,
                                    width: coreSize,
                                    height: coreSize
                                )
                            )
                            context.fill(corePath, with: .color(.white.opacity(0.8)))
                        }
                    }
                }
            }
        }
    }
}

#Preview {
    let gradient = SkyViewModel.createGradient(for: Date(), sunAltitude: -20.0)
    SkyCanvasView(stars: [], gradient: gradient)
}
