import SwiftUI
import shared

/// Canvas view for rendering the starry sky
struct SkyCanvasView: View {
    let stars: [StarViewModel]
    let gradient: [Color]
    let constellations: [ConstellationWithStars]
    let showConstellations: Bool

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
                    // Draw constellation lines first (behind stars)
                    if showConstellations {
                        drawConstellationLines(context: context, size: size)
                    }

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

                    // Draw constellation labels on top
                    if showConstellations {
                        drawConstellationLabels(context: context, size: size)
                    }
                }
            }
        }
    }

    // MARK: - Private Methods

    /// Draw constellation lines
    private func drawConstellationLines(context: GraphicsContext, size: CGSize) {
        // Create a dictionary to quickly look up star positions by HIP ID
        let starPositions = Dictionary(uniqueKeysWithValues: stars.map { ($0.visibleStar.star.id, CGPoint(x: $0.x, y: $0.y)) })

        for constellation in constellations {
            guard !constellation.constellation.lines.isEmpty else { continue }

            for line in constellation.constellation.lines {
                // Get positions of both stars in the line
                // Convert NSString to String for Kotlin interop
                let fromId = line.first as? String ?? String(line.first ?? "")
                let toId = line.second as? String ?? String(line.second ?? "")

                guard let fromPos = starPositions[fromId],
                      let toPos = starPositions[toId] else {
                    continue
                }

                // Only draw if both stars are visible on screen
                guard fromPos.x >= 0 && fromPos.x <= size.width &&
                      fromPos.y >= 0 && fromPos.y <= size.height &&
                      toPos.x >= 0 && toPos.x <= size.width &&
                      toPos.y >= 0 && toPos.y <= size.height else {
                    continue
                }

                // Draw the line
                var path = Path()
                path.move(to: fromPos)
                path.addLine(to: toPos)

                context.stroke(
                    path,
                    with: .color(.white.opacity(0.3)),
                    lineWidth: 1.0
                )
            }
        }
    }

    /// Draw constellation labels
    private func drawConstellationLabels(context: GraphicsContext, size: CGSize) {
        // Create a dictionary to quickly look up star positions by HIP ID
        let starPositions = Dictionary(uniqueKeysWithValues: stars.map { ($0.visibleStar.star.id, CGPoint(x: $0.x, y: $0.y)) })

        for constellation in constellations {
            guard !constellation.constellation.starIds.isEmpty else { continue }

            // Calculate center position of the constellation
            var visibleStars: [CGPoint] = []
            for starId in constellation.constellation.starIds {
                if let pos = starPositions[starId],
                   pos.x >= 0 && pos.x <= size.width &&
                   pos.y >= 0 && pos.y <= size.height {
                    visibleStars.append(pos)
                }
            }

            guard !visibleStars.isEmpty else { continue }

            // Calculate average position
            let centerX = visibleStars.map { $0.x }.reduce(0, +) / CGFloat(visibleStars.count)
            let centerY = visibleStars.map { $0.y }.reduce(0, +) / CGFloat(visibleStars.count)
            let center = CGPoint(x: centerX, y: centerY)

            // Draw constellation name
            let text = Text(constellation.constellation.nameJa)
                .font(.caption)
                .foregroundColor(.white.opacity(0.7))

            context.draw(text, at: center)
        }
    }
}

#Preview {
    let gradient = SkyViewModel.createGradient(for: Date(), sunAltitude: -20.0)
    SkyCanvasView(stars: [], gradient: gradient, constellations: [], showConstellations: true)
}
