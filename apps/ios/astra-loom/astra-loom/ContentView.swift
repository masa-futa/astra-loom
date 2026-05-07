//
//  ContentView.swift
//  astra-loom
//

import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel: SkyViewModel
    @State private var screenSize: CGSize = .zero
    @State private var showLocationPicker = false
    @State private var showTimeControl = false
    @State private var selectedStar: StarViewModel?

    init() {
        _viewModel = StateObject(wrappedValue: SkyViewModel(service: AstraLoomService()))
    }

    var body: some View {
        ZStack {
            // Starry sky canvas
            SkyCanvasView(stars: viewModel.stars, gradient: viewModel.gradient)
                .background(
                    GeometryReader { geometry in
                        Color.clear
                            .onAppear {
                                screenSize = geometry.size
                            }
                            .onChange(of: geometry.size) { _, newSize in
                                screenSize = newSize
                            }
                    }
                )
                .contentShape(Rectangle())
                .onTapGesture { location in
                    // Find tapped star
                    if let tappedStar = findTappedStar(at: location) {
                        withAnimation(.spring(response: 0.4, dampingFraction: 0.8)) {
                            selectedStar = tappedStar
                        }
                    }
                }

            // Overlay UI
            VStack(spacing: 0) {
                // Header
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Astra Loom")
                            .font(.title2)
                            .fontWeight(.bold)

                        Text("\(viewModel.stars.count)個の星")
                            .font(.caption)
                            .opacity(0.8)
                    }
                    .foregroundColor(.white)
                    .shadow(radius: 2)

                    Spacer()

                    HStack(spacing: 12) {
                        // 観測地点ボタン
                        Button {
                            showLocationPicker = true
                        } label: {
                            HStack(spacing: 4) {
                                Image(systemName: "location.fill")
                                Text(viewModel.currentLocation.nameJa)
                                    .font(.caption)
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(.ultraThinMaterial)
                            .cornerRadius(12)
                        }

                        // 時刻コントロールボタン
                        Button {
                            showTimeControl.toggle()
                        } label: {
                            Image(systemName: "clock.fill")
                                .padding(8)
                                .background(.ultraThinMaterial)
                                .cornerRadius(12)
                        }
                    }
                    .foregroundColor(.white)
                }
                .padding()
                .background(.ultraThinMaterial.opacity(0.3))

                // 時刻コントロール（展開時）
                if showTimeControl {
                    TimeControlView(
                        currentTime: $viewModel.currentTime,
                        useCurrentTime: $viewModel.useCurrentTime,
                        timezone: viewModel.currentLocation.timezone,
                        onTimeChange: { _ in
                            Task {
                                await loadStars()
                            }
                        }
                    )
                    .padding(.horizontal)
                    .padding(.bottom)
                    .transition(.move(edge: .top).combined(with: .opacity))
                }

                Spacer()

                // Loading indicator
                if viewModel.isLoading {
                    ProgressView("星空を読み込み中...")
                        .foregroundColor(.white)
                        .padding()
                        .background(.ultraThinMaterial)
                        .cornerRadius(12)
                }

                // Error message
                if let error = viewModel.errorMessage {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                        .padding()
                        .background(.ultraThinMaterial)
                        .cornerRadius(12)
                }

                Spacer()

                // Status bar
                VStack(spacing: 8) {
                    HStack(spacing: 16) {
                        Label(viewModel.currentLocation.nameJa, systemImage: "location.fill")
                        HStack(spacing: 4) {
                            Label(formattedDate, systemImage: "clock.fill")
                            if !viewModel.useCurrentTime {
                                Image(systemName: "lock.fill")
                                    .font(.caption2)
                                    .opacity(0.6)
                            }
                        }
                    }
                    .font(.caption)

                    if !viewModel.skyCondition.isEmpty {
                        HStack(spacing: 12) {
                            Label(viewModel.skyCondition, systemImage: sunIcon)
                            Text("太陽高度: \(String(format: "%.1f°", viewModel.sunAltitude))")
                                .font(.caption2)
                        }
                        .font(.caption2)
                    }
                }
                .foregroundColor(.white.opacity(0.9))
                .padding(.horizontal, 20)
                .padding(.vertical, 12)
                .background(.ultraThinMaterial.opacity(0.3))
                .cornerRadius(20)
                .padding(.bottom, 20)
            }
        }
        .preferredColorScheme(.dark)
        .sheet(isPresented: $showLocationPicker) {
            LocationPickerView(
                currentLocation: viewModel.currentLocation,
                onSelect: { newLocation in
                    viewModel.changeLocation(newLocation)
                    Task {
                        await loadStars()
                    }
                }
            )
        }
        .overlay {
            if selectedStar != nil {
                // 背景タップで閉じる
                Color.black.opacity(0.3)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation(.spring(response: 0.4, dampingFraction: 0.8)) {
                            selectedStar = nil
                        }
                    }
                    .transition(.opacity)
            }
        }
        .overlay(alignment: .trailing) {
            if let star = selectedStar {
                StarDetailView(star: star, onDismiss: {
                    withAnimation(.spring(response: 0.4, dampingFraction: 0.8)) {
                        selectedStar = nil
                    }
                })
                .transition(.move(edge: .trailing))
            }
        }
        .animation(.easeInOut(duration: 0.5), value: viewModel.gradient)
        .task {
            if screenSize != .zero {
                await loadStars()
            }
        }
        .onChange(of: screenSize) { _, newSize in
            if newSize != .zero && viewModel.stars.isEmpty {
                Task {
                    await loadStars()
                }
            }
        }
    }

    private func loadStars() async {
        await viewModel.loadStars(screenSize: screenSize)
    }

    private var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "M/d HH:mm"
        formatter.timeZone = viewModel.currentLocation.timezone
        let time = viewModel.useCurrentTime ? Date() : viewModel.currentTime
        return formatter.string(from: time)
    }

    private var sunIcon: String {
        if viewModel.sunAltitude > 0 {
            return "sun.max.fill"
        } else if viewModel.sunAltitude > -6 {
            return "sun.horizon.fill"
        } else if viewModel.sunAltitude > -18 {
            return "moon.fill"
        } else {
            return "moon.stars.fill"
        }
    }

    /// タップ位置から星を検出
    private func findTappedStar(at location: CGPoint) -> StarViewModel? {
        // タップ判定の許容範囲（ポイント）
        let tapRadius: CGFloat = 20.0

        // 最も近い星を見つける
        return viewModel.stars.min(by: { star1, star2 in
            let distance1 = hypot(star1.x - location.x, star1.y - location.y)
            let distance2 = hypot(star2.x - location.x, star2.y - location.y)
            return distance1 < distance2
        }).flatMap { closestStar in
            // タップ範囲内かチェック
            let distance = hypot(closestStar.x - location.x, closestStar.y - location.y)
            return distance <= tapRadius ? closestStar : nil
        }
    }
}

#Preview {
    ContentView()
}
