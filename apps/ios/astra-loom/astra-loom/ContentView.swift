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
    @State private var showFOVControl = false
    @State private var showSettings = false
    @State private var selectedStar: StarViewModel?
    @State private var isDragging = false
    @State private var lastDragValue: CGSize = .zero
    @State private var isTimeDragging = false
    @State private var lastTimeDragValue: CGFloat = 0
    @State private var lastStarUpdateTime: Date = Date()
    @State private var showTutorial = false

    init() {
        _viewModel = StateObject(wrappedValue: SkyViewModel(service: AstraLoomService()))
    }

    var body: some View {
        ZStack {
            // Starry sky canvas
            SkyCanvasView(
                stars: viewModel.stars,
                gradient: viewModel.gradient,
                constellations: viewModel.constellations,
                showConstellations: viewModel.showConstellations
            )
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

            Color.clear
                .contentShape(Rectangle())
                .simultaneousGesture(
                    DragGesture(minimumDistance: 10)
                        .onChanged { value in
                            isDragging = true
                            let degreesPerPixel = viewModel.viewportState.fieldOfView / screenSize.width

                            // 前回からの差分を計算
                            let deltaWidth = value.translation.width - lastDragValue.width
                            let deltaHeight = value.translation.height - lastDragValue.height

                            let azimuthDelta = -Double(deltaWidth) * degreesPerPixel
                            let altitudeDelta = Double(deltaHeight) * degreesPerPixel

                            viewModel.viewportState.updateAzimuth(azimuthDelta)
                            viewModel.viewportState.updateAltitude(altitudeDelta)

                            // ドラッグ中にリアルタイムで星の位置を更新
                            viewModel.updateStarPositions()

                            lastDragValue = value.translation
                        }
                        .onEnded { _ in
                            isDragging = false
                            lastDragValue = .zero

                            // ドラッグ終了時に念のため再読み込み（視野外の星を更新）
                            Task {
                                await loadStars()
                            }
                        }
                )
                .onTapGesture { location in
                    // Find tapped star (only if not dragging)
                    if !isDragging, let tappedStar = findTappedStar(at: location) {
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
                        // 設定ボタン
                        Button {
                            showSettings = true
                        } label: {
                            Image(systemName: "gearshape.fill")
                                .padding(8)
                                .background(.ultraThinMaterial)
                                .cornerRadius(12)
                        }

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

                        // 星座表示切り替えボタン
                        Button {
                            viewModel.showConstellations.toggle()
                        } label: {
                            Image(systemName: viewModel.showConstellations ? "star.circle.fill" : "star.circle")
                                .padding(8)
                                .background(.ultraThinMaterial)
                                .cornerRadius(12)
                        }

                        // 視野角ボタン
                        Button {
                            showFOVControl.toggle()
                            showTimeControl = false
                        } label: {
                            Image(systemName: "viewfinder")
                                .padding(8)
                                .background(.ultraThinMaterial)
                                .cornerRadius(12)
                        }

                        // 時刻コントロールボタン
                        Button {
                            showTimeControl.toggle()
                            showFOVControl = false
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

                // 視野角コントロール（展開時）
                if showFOVControl {
                    FOVControlView(
                        viewportState: viewModel.viewportState,
                        onFOVChange: {
                            Task {
                                await loadStars()
                            }
                        }
                    )
                    .padding(.horizontal)
                    .padding(.bottom)
                    .transition(.move(edge: .top).combined(with: .opacity))
                }

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

                // コンパス表示
                CompassView(viewportState: viewModel.viewportState)
                    .padding(.top, 8)

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
        .sheet(isPresented: $showSettings) {
            SettingsView(onShowTutorial: {
                showTutorial = true
            })
        }
        .overlay {
            // 2本指ドラッグで時間変更
            TwoFingerDragGestureView(
                onChanged: { deltaX in
                    handleTimeDrag(deltaX: deltaX)
                },
                onEnded: {
                    handleTimeDragEnded()
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
                StarDetailView(
                    star: star,
                    constellations: viewModel.constellations,
                    onDismiss: {
                        withAnimation(.spring(response: 0.4, dampingFraction: 0.8)) {
                            selectedStar = nil
                        }
                    }
                )
                .transition(.move(edge: .trailing))
            }
        }
        .animation(.easeInOut(duration: 0.5), value: viewModel.gradient)
        .fullScreenCover(isPresented: $showTutorial) {
            TutorialView(onDismiss: {
                showTutorial = false
                UserDefaults.standard.set(true, forKey: "hasSeenTutorial")
            })
        }
        .onAppear {
            // 初回起動チェック
            let hasSeenTutorial = UserDefaults.standard.bool(forKey: "hasSeenTutorial")
            if !hasSeenTutorial {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                    showTutorial = true
                }
            }
        }
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

    /// 2本指ドラッグで時間を変更
    private func handleTimeDrag(deltaX: CGFloat) {
        isTimeDragging = true

        // 時間変更速度: 画面幅 = 12時間
        let hoursPerScreenWidth = 12.0
        let hoursChange = -Double(deltaX) / screenSize.width * hoursPerScreenWidth

        // 時刻を変更
        let newTime = viewModel.currentTime.addingTimeInterval(hoursChange * 3600)
        viewModel.changeTime(newTime)

        // スロットリング: 0.2秒ごとに星を更新
        let now = Date()
        if now.timeIntervalSince(lastStarUpdateTime) > 0.2 {
            Task {
                await loadStars()
            }
            lastStarUpdateTime = now
        }
    }

    /// 2本指ドラッグ終了
    private func handleTimeDragEnded() {
        isTimeDragging = false

        // 最終的な星の更新
        Task {
            await loadStars()
        }
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
